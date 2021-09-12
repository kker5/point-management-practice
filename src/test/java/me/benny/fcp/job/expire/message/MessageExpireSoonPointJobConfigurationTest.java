package me.benny.fcp.job.expire.message;

import me.benny.fcp.BatchTestSupport;
import me.benny.fcp.message.Message;
import me.benny.fcp.message.MessageRepository;
import me.benny.fcp.point.Point;
import me.benny.fcp.point.wallet.PointWallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class MessageExpireSoonPointJobConfigurationTest extends BatchTestSupport {
    @Autowired
    Job messageExpireSoonPointJob;
    @Autowired
    MessageRepository messageRepository;

    @Test
    void messageToExpirePointJob() throws Exception {
        // given
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        LocalDate expireDate = LocalDate.of(2021, 9, 4);
        LocalDate notExpireDate = LocalDate.of(2021, 9, 10);
        PointWallet pointWallet1 = pointWalletRepository.save(
                new PointWallet("user1", BigInteger.valueOf(3000))
        );
        PointWallet pointWallet2 = pointWalletRepository.save(
                new PointWallet("user2", BigInteger.valueOf(2000))
        );
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, notExpireDate));
        // when
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2021-09-05")
                .toJobParameters();
        JobExecution execution = launchJob(messageExpireSoonPointJob, jobParameters);
        // then
        then(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        List<Message> messages = messageRepository.findAll();
        then(messages).hasSize(2);
        Message message1 = messages.stream().filter(item -> item.getUserId().equals("user1")).findFirst().orElseGet(null);
        then(message1).isNotNull();
        then(message1.getTitle()).isEqualTo("3000 포인트 만료 예정");
        then(message1.getContent()).isEqualTo("2021-09-12 까지 3000 포인트가 만료 예정입니다.");
        Message message2 = messages.stream().filter(item -> item.getUserId().equals("user2")).findFirst().orElseGet(null);
        then(message2).isNotNull();
        then(message2.getTitle()).isEqualTo("2000 포인트 만료 예정");
        then(message2.getContent()).isEqualTo("2021-09-12 까지 2000 포인트가 만료 예정입니다.");
    }

    @Test
    void messageToExpirePointJob_no_parameter() throws Exception {
        // given
        // when & then
        Assertions.assertThrows(
                JobParametersInvalidException.class,
                () -> launchJob(messageExpireSoonPointJob, null),
                "job parameter today is required"
        );
    }
}