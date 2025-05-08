package kr.hhplus.be.server.interfaces.orderStatistics;

import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsCriteria;
import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsFacade;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatisticsScheduler {
    private final OrderStatisticsFacade orderStatisticsFacade;
    private final OrderStatisticsService orderStatisticsService;
    private final CacheManager cacheManager;

    /**
     * 매일 00시 10분에 전날 통계 데이터 추가
     */
    @Scheduled(cron = "0 10 0 * * *")
    public void aggregateOrderStatistics() {
        LocalDate now = LocalDate.now();
        orderStatisticsFacade.aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate.of(now));

        // 캐시 무효화
        Cache cachedProductIds = cacheManager.getCache("PopularProductIds");
        if (cachedProductIds != null) {
            cachedProductIds.clear();
        } else {
            log.warn("PopularProductIds 캐시를 찾을 수 없습니다.");
        }

        // 캐시 저장
        OrderStatisticsCommand.PopularProductIds command = OrderStatisticsCommand.PopularProductIds.of(now.minusDays(3), now, 5);
        orderStatisticsService.popularProductIds(command);
    }
}
