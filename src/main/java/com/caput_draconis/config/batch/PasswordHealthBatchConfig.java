package com.caput_draconis.config.batch;

import com.caput_draconis.domain.domain.Notification;
import com.caput_draconis.domain.domain.Password;
import com.caput_draconis.domain.entity.NotificationEntity;
import com.caput_draconis.repository.NotificationRepository;
import com.caput_draconis.service.NotificationService;
import jakarta.annotation.Nonnull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;

@Configuration
@EnableBatchProcessing
public class PasswordHealthBatchConfig {

    private final DataSource dataSource;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final JdbcTemplate jdbcTemplate;

    public static final String QUERY_TO_GET_ELIGIBLE_FOR_PASSWORD_RESET =
            "SELECT * FROM passwords WHERE updated_at < CURRENT_DATE - INTERVAL '3 MONTHS'";
    public static final String INIT_QUERY = "UPDATE notifications SET description = '[]'::jsonb WHERE type = 'password_expired'";

    private static final class PasswordBatchConstants{
        public static final String JOB_NAME = "PasswordHealth";
        public static final String INIT_STEP = "InitStep";
        public static final String STEP = "PasswordHealthStep";
        public static final String READER = "ExpiredPasswordReader";
    }

    @Autowired
    public PasswordHealthBatchConfig(DataSource dataSource , NotificationRepository notificationRepository , NotificationService notificationService, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public Job passwordHealthJob(@Nonnull final JobRepository jobRepository,
                                 @Qualifier("clearDescriptionsStep") @Nonnull final Step clearDescriptionStep,
                                 @Nonnull final Step expiredPasswordStep
    ) {
        return new JobBuilder(PasswordBatchConstants.JOB_NAME , jobRepository)
                .start(clearDescriptionStep)
                .next(expiredPasswordStep)
                .build();
    }

    @Bean
    public Step clearDescriptionsStep(@Nonnull final JobRepository jobRepository , @Nonnull final PlatformTransactionManager transactionManager , @Nonnull final JdbcTemplate jdbcTemplate) {
        Tasklet tasklet = (contribution, chunkContext) -> {
            jdbcTemplate.update(INIT_QUERY);
            return RepeatStatus.FINISHED;
        };

        return new StepBuilder(PasswordBatchConstants.INIT_STEP , jobRepository)
                .tasklet(tasklet , transactionManager)
                .build();
    }

    @Bean
    public Step expiredPasswordStep(@Nonnull final JobRepository jobRepository , @Nonnull final PlatformTransactionManager transactionManager) {
        return new StepBuilder(PasswordBatchConstants.STEP , jobRepository)
                .<Password , NotificationEntity>chunk(10 , transactionManager)
                .reader(passwordReader())
                .processor(passwordProcessor(Notification.NotificationType.PASSWORD_EXPIRED))
                .writer(notificationWriter())
                .build();

    }

    @Bean
    public JdbcCursorItemReader<Password> passwordReader() {
        return new JdbcCursorItemReaderBuilder<Password>()
                .name(PasswordBatchConstants.READER)
                .dataSource(dataSource)
                .sql(QUERY_TO_GET_ELIGIBLE_FOR_PASSWORD_RESET)
                .rowMapper(new BeanPropertyRowMapper<>(Password.class))
                .build();
    }

    public ItemProcessor<Password, NotificationEntity> passwordProcessor(@Nonnull final Notification.NotificationType notificationType) {
        return password -> notificationService.convertNotificationToNotificationEntity(Notification.builder()
            .uuid(notificationType.getNotificationType().concat("|").concat(password.getUname()))
            .type(Notification.NotificationType.PASSWORD_EXPIRED)
            .description(List.of(password.getUuid()))
            .username(password.getUname())
            .build());
    }

    @Bean
    public JdbcBatchItemWriter<NotificationEntity> notificationWriter() {
        return new JdbcBatchItemWriterBuilder<NotificationEntity>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                //CAST(:descriptionAsJson AS jsonb)  is equivalent to :descriptionAsJson::jsonb but wont work in springboot
                //COALESCE will return the first non-null value => if notifications.description->'password_expired' is null, return an empty json array
                //jsonb_array_elements_text -> flatten into individual text elements
                //jsonb_agg -> combine into one array
                .sql("""
                        INSERT INTO notifications (uuid , type , description , username)
                        	VALUES(:uuid , :type , CAST(:descriptionAsJson AS jsonb) , :username)
                        	ON CONFLICT(uuid) DO UPDATE
                        	SET description = (
                                SELECT jsonb_agg(DISTINCT expired_passwords)
                                FROM (
                                    SELECT jsonb_array_elements_text(
                                        COALESCE(notifications.description , '[]'::jsonb) ||
                                        COALESCE(EXCLUDED.description , '[]'::jsonb)
                                    ) AS expired_passwords
                                ) AS merged
                            )
                """)
                .dataSource(dataSource)
                .build();
    }
}
