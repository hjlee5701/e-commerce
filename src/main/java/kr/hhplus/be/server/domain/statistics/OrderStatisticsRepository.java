package kr.hhplus.be.server.domain.statistics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface OrderStatisticsRepository {

    OrderStatistics save(OrderStatistics statistics);

    Page<PopularProductsProjection> findPopularProductsForDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<OrderStatistics> getByProductIdsAndDate(LocalDate startDate, LocalDate endDate, Set<Long> longs);

    Page<Long> findPopularProductIdsForDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean incrementProductScoreByDate(String dateKey, Long productId, Integer quantity);

    Set<ZSetOperations.TypedTuple<String>> getTopProducts(String dateKey, int topN);


    void aggregateTopProductsByPeriod(String rankingKey, List<String> dailyKeys);
}
