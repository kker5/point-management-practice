package me.benny.fcp.job.validator;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class TodayJobParameterValidatorTest {

    @Test
    void success() {
        JobParametersValidator validator = new TodayJobParameterValidator();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2021-01-05")
                .toJobParameters();
        assertDoesNotThrow(
                () -> validator.validate(jobParameters)
        );
    }

    @Test
    void no_jobparameter() {
        JobParametersValidator validator = new TodayJobParameterValidator();
        Exception exception = assertThrows(
                JobParametersInvalidException.class,
                () -> validator.validate(null)
        );
        then(exception.getMessage()).isEqualTo("job parameter today is required");
    }

    @Test
    void no_jobparameter_today() {
        JobParametersValidator validator = new TodayJobParameterValidator();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("no_today", "2021-01-05")
                .toJobParameters();
        Exception exception = assertThrows(
                JobParametersInvalidException.class,
                () -> validator.validate(jobParameters)
        );
        then(exception.getMessage()).isEqualTo("job parameter today is required");
    }

    @Test
    void wrong_date_format() {
        JobParametersValidator validator = new TodayJobParameterValidator();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2021/01/05")
                .toJobParameters();
        Exception exception = assertThrows(
                JobParametersInvalidException.class,
                () -> validator.validate(jobParameters)
        );
        then(exception.getMessage()).isEqualTo("job parameter today format is not valid");
    }
}