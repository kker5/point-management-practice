package me.benny.fcp.point.reservation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.benny.fcp.point.IdEntity;
import me.benny.fcp.point.wallet.PointWallet;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PointReservation extends IdEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_wallet_id", nullable = false)
    PointWallet pointWallet;
    // 적립금액
    @Column(name = "amount", nullable = false)
    BigInteger amount;
    // 적립일자
    @Column(name = "earned_date", nullable = false)
    LocalDate earnedDate;
    // 유효일
    @Column(name = "available_days", nullable = false)
    int available_days;
    // 실행여부
    @Column(name = "is_applied", columnDefinition = "TINYINT", length = 1, nullable = false)
    boolean applied;
}