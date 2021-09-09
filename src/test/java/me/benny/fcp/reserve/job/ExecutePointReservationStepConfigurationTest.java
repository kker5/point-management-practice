package me.benny.fcp.reserve.job;

import me.benny.fcp.BatchTestSupport;
import me.benny.fcp.point.Point;
import me.benny.fcp.point.PointRepository;
import me.benny.fcp.point.reservation.PointReservation;
import me.benny.fcp.point.reservation.PointReservationRepository;
import me.benny.fcp.point.wallet.PointWallet;
import me.benny.fcp.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class ExecutePointReservationStepConfigurationTest extends BatchTestSupport {
    @Autowired
    PointReservationRepository pointReservationRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    Job executePointReservationJob;

    @Test
    void executePointReservationStep() throws Exception {
        LocalDate earnDate = LocalDate.of(2021, 1, 5);
        PointWallet pointWallet1 = pointWalletRepository.save(
                new PointWallet("user1", BigInteger.valueOf(3000))
        );
        pointReservationRepository.save(
                new PointReservation(
                        pointWallet1,
                        BigInteger.valueOf(1000),
                        earnDate,
                        10
                )
        );
        pointReservationRepository.save(
                new PointReservation(
                        pointWallet1,
                        BigInteger.valueOf(500),
                        earnDate.minusDays(1),
                        3
                )
        );
        pointReservationRepository.save(
                new PointReservation(
                        pointWallet1,
                        BigInteger.valueOf(700),
                        earnDate.plusDays(1),
                        1
                )
        );
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2021-01-05")
                .toJobParameters();
        JobExecution execution = launchJob(executePointReservationJob, jobParameters);
        // then
        // then point 적립 2개 확인
        then(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        List<Point> points = pointRepository.findAll();
        then(points).hasSize(2);
        Point point1 = points.stream().filter(it -> it.getAmount().compareTo(BigInteger.valueOf(1000)) == 0).findAny().orElse(null);
        then(point1).isNotNull();
        then(point1.getEarnedDate()).isEqualTo(LocalDate.of(2021, 1, 5));
        then(point1.getExpireDate()).isEqualTo(LocalDate.of(2021, 1, 15));
        then(point1.isExpired()).isFalse();
        then(point1.isUsed()).isFalse();
        Point point2 = points.stream().filter(it -> it.getAmount().compareTo(BigInteger.valueOf(500)) == 0).findAny().orElse(null);
        then(point2).isNotNull();
        then(point2.getEarnedDate()).isEqualTo(LocalDate.of(2021, 1, 4));
        then(point2.getExpireDate()).isEqualTo(LocalDate.of(2021, 1, 7));
        then(point2.isExpired()).isFalse();
        then(point2.isUsed()).isFalse();
        // PointWallet의 잔액 확인 3000 -> 4500
        List<PointWallet> wallets = pointWalletRepository.findAll();
        then(wallets).hasSize(1);
        then(wallets.get(0).getAmount()).isEqualByComparingTo(BigInteger.valueOf(4500));
        // reservation 2개 완료처리되었는지 확인
        List<PointReservation> reservations = pointReservationRepository.findAll();
        then(reservations).hasSize(3);
        then(reservations.stream().filter(it -> it.isExecuted())).hasSize(2);
    }
}