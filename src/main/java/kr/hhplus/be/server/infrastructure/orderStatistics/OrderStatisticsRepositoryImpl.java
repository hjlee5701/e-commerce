package kr.hhplus.be.server.infrastructure.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsRepository;
import kr.hhplus.be.server.domain.statistics.PopularProductsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class OrderStatisticsRepositoryImpl implements OrderStatisticsRepository {

    private final OrderStatisticsJpaRepository orderStatisticsJpaRepository;


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
}
