package me.benny.fcp.point;

import lombok.*;
import me.benny.fcp.point.wallet.PointWallet;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Point extends IdEntity {
    // 포인트 지갑
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_wallet_id", nullable = false)
    PointWallet pointWallet;
    // 적립금액
    @Column(name = "amount", nullable = false, columnDefinition = "BIGINT")
    BigInteger amount;
    // 적립일자
    @Column(name = "earned_date", nullable = false)
    LocalDate earnedDate;
    // 만료일자
    @Column(name = "expire_date", nullable = false)
    LocalDate expireDate;
    // 사용유무
    @Column(name = "is_used", nullable = false, columnDefinition = "TINYINT", length = 1)
    boolean used;
    // 만료유무
    @Setter
    @Column(name = "is_expired", nullable = false, columnDefinition = "TINYINT", length = 1)
    boolean expired;

    public Point(
            PointWallet pointWallet,
            BigInteger amount,
            LocalDate earnedDate,
            LocalDate expireDate
    ) {
        this.pointWallet = pointWallet;
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.expireDate = expireDate;
        this.used = false;
        this.expired = false;
    }
}