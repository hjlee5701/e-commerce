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
    public Page<PopularProductsProjection> findPopularProductsForDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderStatisticsJpaRepository.findPopularProductsForDateRange(startDate, endDate, pageable);
    }
}
