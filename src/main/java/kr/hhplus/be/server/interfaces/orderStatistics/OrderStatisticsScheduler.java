package kr.hhplus.be.server.interfaces.orderStatistics;

import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsCriteria;
import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrderStatisticsScheduler {
    private final OrderStatisticsFacade orderStatisticsFacade;

    /**
     * 매일 00시 10분에 전날 통계 데이터 추가
     */
    @Scheduled(cron = "0 10 0 * * *")
    public void aggregateOrderStatistics() {
        LocalDate endDate = LocalDate.now();
        orderStatisticsFacade.aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate.of(endDate));
    }
}
