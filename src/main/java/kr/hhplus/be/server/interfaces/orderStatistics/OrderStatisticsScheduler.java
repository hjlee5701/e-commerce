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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

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
//    @Scheduled(cron = "0 10 0 * * *")
    public void aggregateOrderStatistics() {
        LocalDate now = LocalDate.now();
        orderStatisticsFacade.aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate.of(now));

        // 캐시 무효화
        Cache cachedProducts = cacheManager.getCache("Ranking");
        if (cachedProducts != null) {
            cachedProducts.clear();
        } else {
            log.warn("Ranking 캐시를 찾을 수 없습니다.");
        }

        // 캐시 저장
        OrderStatisticsCommand.Popular command = OrderStatisticsCommand.Popular.of(now.minusDays(3), now, 5);
        orderStatisticsService.popular(command);
    }


//    @Scheduled(fixedDelay = 1000000)
    @Scheduled(cron = "0 15 0 * * *")
    public void aggregateOrderStatisticsWithRedis() {
        LocalDate yesterday = LocalDate.now().minusDays(1);  // 전일 기준
        String rankingKey = "product:ranking:3d:" + yesterday.format(DateTimeFormatter.BASIC_ISO_DATE);

        try {
            // 최근 3일 일별 키 생성
            List<String> dailyKeys = IntStream.rangeClosed(0, 2)
                    .mapToObj(i -> "product:ranking:daily:" + yesterday.minusDays(i).format(DateTimeFormatter.BASIC_ISO_DATE))
                    .toList();

            if (dailyKeys.isEmpty()) {
                log.warn("No daily keys found for union");
                return;
            }
            // 전날 통계 생성
            orderStatisticsFacade.dailyOrderStatisticsWithRedis(OrderStatisticsCriteria.Aggregate.of(yesterday));

            // ZUNIONSTORE 수행 (3일치 집계)
            orderStatisticsService.aggregateTopProductsByPeriod(rankingKey, dailyKeys);

            // 인기 상품 조회 캐싱
            orderStatisticsFacade.popularWithRedis();

            log.info("Updated 3-day ranking key: {}", rankingKey);
        } catch (Exception e) {
            log.error("Error updating 3-day ranking", e);
            // 필요시 알림, 재시도 로직 추가 가능
        }
    }

}
