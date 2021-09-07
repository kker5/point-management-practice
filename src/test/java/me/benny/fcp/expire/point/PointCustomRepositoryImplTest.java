package me.benny.fcp.expire.point;

import me.benny.fcp.expire.point.wallet.PointWallet;
import me.benny.fcp.expire.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PointCustomRepositoryImplTest {
    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    PointRepository pointRepository;

    @Test
    void sumByExpiredDate() {
        // given
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        LocalDate expireDate = LocalDate.of(2021, 1, 3);
        LocalDate notExpireDate = LocalDate.of(2025, 12, 31);
        PointWallet pointWallet1 = pointWalletRepository.save(
                new PointWallet("user1", BigInteger.valueOf(3000))
        );
        PointWallet pointWallet2 = pointWalletRepository.save(
                new PointWallet("user2", BigInteger.ZERO)
        );
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        // when
        Page<ExpiredPointSummary> expiredPointSummaries = pointRepository.sumByExpiredDate(expireDate, PageRequest.of(0, 10));

        // then
        then(expiredPointSummaries.getTotalElements()).isEqualTo(2);
        then(expiredPointSummaries.getTotalPages()).isEqualTo(1);
        then(expiredPointSummaries.getSize()).isEqualTo(10);
        // then elements
        ExpiredPointSummary expiredPointSummary1 = expiredPointSummaries.getContent().stream().filter(item -> item.userId.equals("user1")).findFirst().orElseGet(null);
        then(expiredPointSummary1).isNotNull();
        then(expiredPointSummary1.amount).isEqualTo(3000);
        ExpiredPointSummary expiredPointSummary2 = expiredPointSummaries.getContent().stream().filter(item -> item.userId.equals("user2")).findFirst().orElseGet(null);
        then(expiredPointSummary2).isNotNull();
        then(expiredPointSummary2.amount).isEqualTo(2000);
    }

    @Test
    void sumBeforeExpireDate() {
        // given
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        LocalDate expireDate = LocalDate.of(2021, 1, 3);
        LocalDate notExpireDate = LocalDate.of(2025, 12, 31);
        PointWallet pointWallet1 = pointWalletRepository.save(
                new PointWallet("user1", BigInteger.valueOf(3000))
        );
        PointWallet pointWallet2 = pointWalletRepository.save(
                new PointWallet("user2", BigInteger.valueOf(2000))
        );
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000), earnDate, notExpireDate));
        // when
        Page<ExpiredPointSummary> expiredPointSummaries = pointRepository.sumBeforeExpireDate(notExpireDate.plusDays(1), PageRequest.of(0, 10));

        // then
        then(expiredPointSummaries.getTotalElements()).isEqualTo(2);
        then(expiredPointSummaries.getTotalPages()).isEqualTo(1);
        then(expiredPointSummaries.getSize()).isEqualTo(10);
        // then elements
        ExpiredPointSummary expiredPointSummary1 = expiredPointSummaries.getContent().stream().filter(item -> item.userId.equals("user1")).findFirst().orElseGet(null);
        then(expiredPointSummary1).isNotNull();
        then(expiredPointSummary1.amount).isEqualTo(3000);
        ExpiredPointSummary expiredPointSummary2 = expiredPointSummaries.getContent().stream().filter(item -> item.userId.equals("user2")).findFirst().orElseGet(null);
        then(expiredPointSummary2).isNotNull();
        then(expiredPointSummary2.amount).isEqualTo(2000);
    }
}