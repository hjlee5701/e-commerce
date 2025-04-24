package kr.hhplus.be.server.domain.statistics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface OrderStatisticsRepository {

    OrderStatistics save(OrderStatistics statistics);

    Page<PopularProductsProjection> findPopularProductsForDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<OrderStatistics> getByProductIdsAndDate(LocalDate startDate, LocalDate endDate, Set<Long> longs);
}
