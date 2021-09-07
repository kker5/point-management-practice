package me.benny.fcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 포인트 만료 시스템
 * ex)
 * java -jar batch.jar --job.name=expirePointJob today=2021-01-01
 */
@SpringBootApplication
@Slf4j
public class ExpirePointApplication {
    public static void main(String[] args) {
        log.info("application arguments : " + String.join(",", args));
        SpringApplication.run(ExpirePointApplication.class, args);
    }
}