package me.benny.fcp.job.expire.message;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import me.benny.fcp.point.PointRepository;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MessageExpiredPointStepPartitioner implements Partitioner {
    private final PointRepository pointRepository;
    private final LocalDate today;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        // 오늘 알림 나가야할 모든 포인트 지갑 ID를 가져옴
        List<Long> pointWalletIds = pointRepository.findDistinctWalletIdForExpiredPoint(today);
        Map<String, ExecutionContext> result = new HashMap<>();
        // 포인트 지갑 ID를 gridSize개수 만큼 나눔
        List<List<Long>> splitedPointWalletIds = Lists.partition(pointWalletIds, gridSize);
        for (int i = 0; i < splitedPointWalletIds.size(); i++) {
            List<Long> ids = splitedPointWalletIds.get(i);
            ExecutionContext value = new ExecutionContext();
            // 나눈 포인트 지갑 ID List 를 ids에 넣음
            value.put("ids", ids);
            result.put("partition" + i, value);
        }
        return result;
    }
}
