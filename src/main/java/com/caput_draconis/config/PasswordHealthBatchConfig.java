package com.caput_draconis.config;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;

@Configuration
@EnableBatchProcessing
public class PasswordHealthBatchConfig {

    private final DataSource dataSource;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public static final String QUERY_TO_GET_ELIGIBLE_FOR_PASSWORD_RESET =
            "SELECT * FROM passwords WHERE updated_at < CURRENT_DATE - INTERVAL '3 MONTHS'";

    static final class PasswordBatchConstants{
        public static final String JOB_NAME = "PasswordHealth";
        public static final String STEP = "PasswordHealthStep";
        public static final String READER = "ExpiredPasswordReader";
    }

    @Autowired
    public PasswordHealthBatchConfig(DataSource dataSource , NotificationRepository notificationRepository , NotificationService notificationService) {
        this.dataSource = dataSource;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @Bean
    public Job passwordHealthJob(@Nonnull final JobRepository jobRepository , @Nonnull final Step expiredPasswordStep) {
        return new JobBuilder(PasswordBatchConstants.JOB_NAME , jobRepository)
                .start(expiredPasswordStep)
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
        return password -> {
            Optional<NotificationEntity> existingNotification = notificationRepository.findNotificationByUuid(notificationType.getNotificationType().concat("|").concat(password.getUname()));
            if(existingNotification.isPresent()) {
                Object passwordUuids = existingNotification.get().getDescription().get(notificationType.getNotificationType());
                Set<String> passwordUuidsList = (Set<String>) passwordUuids;
                passwordUuidsList.add(password.getUuid());
                return existingNotification.get();
            }
            else{
                return notificationService.convertNotificationToNotificationEntity(Notification.builder()
                        .uuid(notificationType.getNotificationType().concat("|").concat(password.getUname()))
                        .type(Notification.NotificationType.PASSWORD_EXPIRED)
                        .description(Map.of(Notification.NotificationType.PASSWORD_EXPIRED.getNotificationType() , Set.of(password.getUuid())))
                        .username(password.getUname())
                        .build());
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<NotificationEntity> notificationWriter() {
        return new JdbcBatchItemWriterBuilder<NotificationEntity>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                        INSERT INTO notifications (uuid , type , description , username)
                        VALUES(:uuid , :type , CAST(:descriptionAsJson AS jsonb) , :username)
                        ON CONFLICT(uuid) DO UPDATE SET description = EXCLUDED.description
                    """)
                .dataSource(dataSource)
                .build();
    }
}
