package kr.hhplus.be.server.infrastructure.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsRepository;
import kr.hhplus.be.server.domain.statistics.PopularProductsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class OrderStatisticsRepositoryImpl implements OrderStatisticsRepository {

    private final OrderStatisticsJpaRepository orderStatisticsJpaRepository;
    private final OrderStatisticsRedisRepository orderStatisticsRedisRepository;


    @Override
    public OrderStatistics save(OrderStatistics statistics) {
        return orderStatisticsJpaRepository.save(statistics);
    }

    @Override
    public Page<PopularProductsProjection> findPopularProductsForDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderStatisticsJpaRepository.findPopularProductsForDateRange(startDate, endDate, pageable);
    }

    @Override
    public List<OrderStatistics> getByProductIdsAndDate(LocalDate startDate, LocalDate endDate, Set<Long> productIds) {
        return orderStatisticsJpaRepository.findProductIdsAndDate(startDate, endDate, productIds);
    }

    @Override
    public Page<Long> findPopularProductIdsForDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderStatisticsJpaRepository.findPopularProductIdsForDateRange(startDate, endDate, pageable);
    }

    @Override
    public boolean incrementProductScoreByDate(String dateKey, Long productId, Integer quantity) {
        return orderStatisticsRedisRepository.incrementProductScore(dateKey, productId, quantity);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getTopProducts(String dateKey, int topN) {
        return orderStatisticsRedisRepository.getTopProducts(dateKey, topN);
    }

    @Override
    public void aggregateTopProductsByPeriod(String rankingKey, List<String> dailyKeys) {
        orderStatisticsRedisRepository.aggregateTopProductsByPeriod(rankingKey, dailyKeys);
    }
}
