package me.benny.fcp.expire.point.wallet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.benny.fcp.expire.point.IdEntity;
import me.benny.fcp.expire.point.Point;

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
    @Column(name = "amount", columnDefinition = "BIGINT")
    BigInteger amount;

    public void expire(Point point) {
        if (this.getId().equals(point.getPointWallet().getId()) && point.isExpired()) {
            this.amount = this.amount.subtract(point.getAmount());
        }
    }
}