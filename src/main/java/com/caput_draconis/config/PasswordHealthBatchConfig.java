package com.caput_draconis.config;

import com.caput_draconis.domain.domain.Password;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configurable
@EnableBatchProcessing
public class PasswordHealthBatchConfig {

    private final JobRepository jobRepository;
    private final DataSource dataSource;
    public static final String JOB_NAME = "PasswordHealthBatch";
    public static final String PASSWORD_READER_JOB_NAME = "PasswordHealthBatchJob";
    public static final String QUERY_TO_GET_ELIGIBLE_FOR_PASSWORD_RESET =
            "SELECT uuid FROM passwords WHERE updated_at < CURRENT_DATE - INTERVAL '3 MONTHS'";

    @Autowired
    public PasswordHealthBatchConfig(JobRepository jobRepository , DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.dataSource = dataSource;
    }

//    public Job passwordHealthJob() {
//        return new JobBuilder(JOB_NAME , jobRepository)
//                .
//    }

    public JdbcCursorItemReader<Password> passwordReader() {
        return new JdbcCursorItemReaderBuilder<Password>()
                .name(PASSWORD_READER_JOB_NAME)
                .dataSource(dataSource)
                .sql(QUERY_TO_GET_ELIGIBLE_FOR_PASSWORD_RESET)
                .rowMapper(new BeanPropertyRowMapper<>(Password.class))
                .build();
    }

    public ItemProcessor<Password, Password> passwordProcessor() {
        return item -> {
            Password password = new Password();
            password.setPassword(item.getPassword());
            return password;
        };
    }
}
