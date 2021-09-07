package me.benny.fcp.point;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ExpiredPointSummary {
    String userId;
    BigInteger amount;

    @QueryProjection
    public ExpiredPointSummary(
            String userId,
            BigInteger amount
    ) {
        this.userId = userId;
        this.amount = amount;
    }
}
