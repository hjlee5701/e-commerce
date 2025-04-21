package kr.hhplus.be.server.domain.statistics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
public interface OrderStatisticsRepository {

    OrderStatistics save(OrderStatistics statistics);

    Page<PopularProductsProjection> findPopularProductsForDateRange(int days, Pageable pageable);
    Page<PopularProductsProjection> findPopularProductsForDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
