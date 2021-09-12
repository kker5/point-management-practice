package me.benny.fcp.point.wallet;

import lombok.*;
import me.benny.fcp.point.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PointWallet extends IdEntity {
    // user 식별자
    @Column(name = "user_id", unique = true, nullable = false)
    String userId; // 임의로 결정
    // 포인트 금액
    @Setter
    @Column(name = "amount", columnDefinition = "BIGINT")
    BigInteger amount;
}