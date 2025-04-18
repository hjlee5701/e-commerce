package kr.hhplus.be.server.infrastructure.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsRepository;
import kr.hhplus.be.server.domain.statistics.PopularProductsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class OrderStatisticsRepositoryImpl implements OrderStatisticsRepository {

    private final OrderStatisticsJpaRepository orderStatisticsJpaRepository;


    @Override
    public OrderStatistics save(OrderStatistics statistics) {
        return orderStatisticsJpaRepository.save(statistics);
    }

    @Override
    public Page<PopularProductsProjection> findPopularProductsForDateRange(int days, Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.minusDays(days);
        return orderStatisticsJpaRepository.findPopularProductsForDateRange(startDate, endDate, pageable);
    }
}
