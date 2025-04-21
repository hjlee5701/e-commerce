package kr.hhplus.be.server.domain.statistics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderStatisticsRepository {

    OrderStatistics save(OrderStatistics statistics);

    Page<PopularProductsProjection> findPopularProductsForDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<OrderStatistics> getByProductIdsAndDate(LocalDateTime statisticsAt, Set<Long> longs);
}
