package me.benny.fcp.job.expire.message;

import me.benny.fcp.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 만료된 포인트에 대하여 공지하도록 message 테이블에 저장하는 Job
 */
@Configuration
public class MessageExpiredPointJobConfiguration {
    @Bean
    public Job messageExpiredPointJob(
            JobBuilderFactory jobBuilderFactory,
            TodayJobParameterValidator validator,
            Step messageExpiredPointStep
    ) {
        return jobBuilderFactory
                .get("messageExpiredPointJob")
                .validator(validator)
                .incrementer(new RunIdIncrementer())
                .start(messageExpiredPointStep)
                .build();
    }
}
