package me.benny.fcp;

import me.benny.fcp.message.MessageRepository;
import me.benny.fcp.point.PointRepository;
import me.benny.fcp.point.reservation.PointReservationRepository;
import me.benny.fcp.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BatchTestSupport {

    @Autowired
    protected JobRepository jobRepository;
    @Autowired
    protected JobLauncher jobLauncher;
    @Autowired
    protected PointWalletRepository pointWalletRepository;
    @Autowired
    protected PointRepository pointRepository;
    @Autowired
    protected MessageRepository messageRepository;
    @Autowired
    protected PointReservationRepository pointReservationRepository;

    protected JobExecution launchJob(Job job, JobParameters jobParameters) throws Exception {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(job);
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJobRepository(jobRepository);
        return jobLauncherTestUtils.launchJob(jobParameters == null ? new JobParametersBuilder().toJobParameters() : jobParameters);
    }

    @AfterEach
    protected void deleteAll() {
        pointRepository.deleteAll();
        pointReservationRepository.deleteAll();
        pointWalletRepository.deleteAll();
        messageRepository.deleteAll();
    }
}
