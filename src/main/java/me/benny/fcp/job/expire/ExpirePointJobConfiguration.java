package me.benny.fcp.job.expire;

import me.benny.fcp.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 금일 기준으로 유효기간이 지난 포인트들을 모두 만료시키고 포인트지갑에서 금액을 차감함
 */
@Configuration
public class ExpirePointJobConfiguration {
    @Bean
    public Job expirePointJob(
            JobBuilderFactory jobBuilderFactory,
            TodayJobParameterValidator validator,
            Step expirePointStep
    ) {
        return jobBuilderFactory
                .get("expirePointJob")
                .validator(validator)
                .incrementer(new RunIdIncrementer())
                .start(expirePointStep)
                .build();
    }
}
