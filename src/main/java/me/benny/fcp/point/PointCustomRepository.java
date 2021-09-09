package me.benny.fcp.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PointCustomRepository {
    /**
     * 만료일자가 일치하는 대상들을 userId로 groupBy하여 sum한 결과 값을 Pagination하여 반환함
     *
     * @param alarmCriteriaDate 알림전송할 대상 일자
     * @param pageable   Page요청
     * @return PExpiredPoint의 Page 결과
     */
    Page<ExpiredPointSummary> sumByExpiredDate(LocalDate alarmCriteriaDate, Pageable pageable);

    Page<ExpiredPointSummary> sumBeforeExpireDate(LocalDate alarmCriteriaDate, Pageable pageable);
}
