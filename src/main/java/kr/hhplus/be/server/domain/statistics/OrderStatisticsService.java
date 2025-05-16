package kr.hhplus.be.server.domain.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderStatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;

    @Cacheable(
            value = "Ranking",
            key = "'Ranking' + #command.startDate + ':' + #command.endDate + ':' + #command.count"
    )
    public List<OrderStatisticsInfo.Popular> popular(OrderStatisticsCommand.Popular command) {
        Pageable pageable = PageRequest.of(0, command.getCount());

        Page<PopularProductsProjection> popularPage = orderStatisticsRepository.findPopularProductsForDateRange(command.getStartDate(), command.getEndDate(), pageable);

        List<PopularProductsProjection> populars = popularPage.getContent();
        return IntStream.range(0, populars.size())
                .mapToObj(i -> OrderStatisticsInfo.Popular.of(i + 1, populars.get(i)))
                .toList();
    }

    @Cacheable(
            value = "Cache",
            key = "'Ranking:3d:' + #command.yesterday + ':' + #command.topN"
    )
    public Map<Long, Double> popularWithRedis(OrderStatisticsCommand.PopularWithRedis command) {

        String dateKey = "product:ranking:" + command.getDays() + "Days:" +command.getYesterday();
        // 2. 최근 3일 간 인기 상품 상위 5개 조회
        Set<ZSetOperations.TypedTuple<String>> topProducts = orderStatisticsRepository.getTopProducts(dateKey, command.getTopN());

        // 3. 상위 5개 상품 ID와 판매량을 출력
        return topProducts.stream()
                .collect(Collectors.toMap(
                        product -> Long.parseLong(product.getValue()),
                        ZSetOperations.TypedTuple::getScore
                ));
    }



    public void aggregate(OrderStatisticsCommand.Aggregate command) {

        Map<Long, Integer> soldProduct = command.toSoldProductMap();
        LocalDate startDate = command.getStartDate();
        LocalDate endDate = command.getEndDate();

        // 통계 데이터 가져오기
        List<OrderStatistics> orderStatistics =
                orderStatisticsRepository.getByProductIdsAndDate(startDate, endDate, soldProduct.keySet());

        Map<Long, OrderStatistics> statisticsMap = orderStatistics.stream()
                .collect(Collectors.toMap(
                        stat -> stat.getProduct().getId(),
                        Function.identity()
                ));

        for (Map.Entry<Long, Integer> entry : soldProduct.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            OrderStatistics statistics = statisticsMap.get(productId);

            if (statistics != null) {
                statistics.aggregateQuantity(quantity);
            } else {
                OrderStatistics newStat = OrderStatistics.create(productId, quantity, command.getEndDate());
                orderStatisticsRepository.save(newStat);
            }
        }
    }

    public void aggregateTopProductsByPeriod(String rankingKey, List<String> dailyKeys) {
        orderStatisticsRepository.aggregateTopProductsByPeriod(rankingKey, dailyKeys);
    }
    public void dailyOrderStatistics(OrderStatisticsCommand.Aggregate command) {
        LocalDate endDate = command.getEndDate();

        Map<Long, Integer> soldProduct = command.toSoldProductMap();
        String redisKey = "product:ranking:daily:" + endDate.toString();

        for (Map.Entry<Long, Integer> entry : soldProduct.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            orderStatisticsRepository.incrementProductScoreByDate(redisKey, productId, quantity);
        }
    }


    /**
     * 결제 완료(커밋 이후)될 경우, 호출되는 비동기 이벤트 메서드
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paidProductAggregateWithRedis(OrderStatisticsCommand.AggregatePaidProduct command) {
        Map<Long, Integer> soldProduct = command.getProductMap();
        LocalDateTime startDateTime = command.getStartDateTime();

        // 1. 주문 날짜
        try {
            for (Map.Entry<Long, Integer> entry : soldProduct.entrySet()) {
                String dateKey = "product:ranking:" + startDateTime.toString(); // 날짜별 키

                // 판매량을 ZSet에 기록
                boolean success = orderStatisticsRepository.incrementProductScoreByDate(dateKey, entry.getKey(), entry.getValue());
                if (!success) {
                    // 실패 시 로깅 또는 재시도 로직 추가
                    log.error("Failed to increment score for productId: " + entry.getKey());
                }
            }
        } catch (Exception e) {
            log.error("Redis 집계 중 예외 발생", e);
            throw e; // 필요에 따라 재처리 또는 예외 전파
        }
    }


//    @Cacheable(
//            value = "PopularProductIds",
//            key = "'PopularProductIds:' + #command.startDate + ':' + #command.endDate + ':' + #command.count"
//    )
    public List<Long> popularProductIds(OrderStatisticsCommand.Popular command) {
        // 인기 상품의 ID 만 조회
        Pageable pageable = PageRequest.of(0, command.getCount());
        Page<Long> popularPage
                = orderStatisticsRepository.findPopularProductIdsForDateRange(
                        command.getStartDate(), command.getEndDate(), pageable);
        return popularPage.getContent();
    }

}