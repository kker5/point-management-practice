package me.benny.fcp.job.reservation;

import me.benny.fcp.job.reader.ReverseJpaPagingItemReader;
import me.benny.fcp.job.reader.ReverseJpaPagingItemReaderBuilder;
import me.benny.fcp.point.Point;
import me.benny.fcp.point.PointRepository;
import me.benny.fcp.point.reservation.PointReservation;
import me.benny.fcp.point.reservation.PointReservationRepository;
import me.benny.fcp.point.wallet.PointWallet;
import me.benny.fcp.point.wallet.PointWalletRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
public class ExecutePointReservationStepConfiguration {

    /**
     * 파티셔닝 사용
     * 단, 동시성 문제가 있어서 사용이 불가합니다.
     */
    @Bean
    @JobScope
    public Step executePointReservationMasterStep(
            StepBuilderFactory stepBuilderFactory,
            TaskExecutorPartitionHandler partitionHandler,
            PointReservationRepository pointReservationRepository,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}")
                    LocalDate today
    ) {
        return stepBuilderFactory
                .get("executePointReservationMasterStep")
                .partitioner(
                        "executePointReservationStep",
                        new ExecutePointReservationStepPartitioner(pointReservationRepository, today)
                )
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(
            Step executePointReservationStep,
            TaskExecutor taskExecutor
    ) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(executePointReservationStep);
        partitionHandler.setGridSize(8);
        partitionHandler.setTaskExecutor(taskExecutor);
        return partitionHandler;
    }

    @Bean
    public Step executePointReservationStep(
            StepBuilderFactory stepBuilderFactory,
            PlatformTransactionManager platformTransactionManager,
            ReverseJpaPagingItemReader<PointReservation> executePointReservationItemReader,
            ItemProcessor<PointReservation, Pair<PointReservation, Point>> executePointReservationItemProcessor,
            ItemWriter<Pair<PointReservation, Point>> executePointReservationItemWriter
    ) {
        return stepBuilderFactory
                .get("executePointReservationStep")
                .allowStartIfComplete(true)
                .transactionManager(platformTransactionManager)
                .<PointReservation, Pair<PointReservation, Point>>chunk(1000)
                .reader(executePointReservationItemReader)
                .processor(executePointReservationItemProcessor)
                .writer(executePointReservationItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ReverseJpaPagingItemReader<PointReservation> executePointReservationItemReader(
            PointReservationRepository pointReservationRepository,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}") LocalDate today,
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) {
        return new ReverseJpaPagingItemReaderBuilder<PointReservation>()
                .name("messageExpireSoonPointItemReader")
                .query(
                        pageable -> pointReservationRepository.findPointReservationToExecute(today, minId, maxId, pageable)
                )
                .pageSize(1000)
                .sort(Sort.by(Sort.Direction.ASC, "id"))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<PointReservation, Pair<PointReservation, Point>> executePointReservationItemProcessor() {
        return reservation -> {
            reservation.setExecuted(true);
            Point earnedPoint = new Point(
                    reservation.getPointWallet(),
                    reservation.getAmount(),
                    reservation.getEarnedDate(),
                    reservation.getExpireDate()
            );
            PointWallet wallet = earnedPoint.getPointWallet();
            wallet.setAmount(wallet.getAmount().add(earnedPoint.getAmount()));
            return Pair.of(reservation, earnedPoint);
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Pair<PointReservation, Point>> executePointReservationItemWriter(
            PointReservationRepository pointReservationRepository,
            PointRepository pointRepository,
            PointWalletRepository pointWalletRepository
    ) {
        return reservationAndPoints -> {
            for (Pair<PointReservation, Point> pair : reservationAndPoints) {
                PointReservation reservation = pair.getFirst();
                Point point = pair.getSecond();
                pointReservationRepository.save(reservation);
                pointRepository.save(point);
                pointWalletRepository.save(reservation.getPointWallet());
            }
        };
    }
}