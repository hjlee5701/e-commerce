package kr.hhplus.be.server.infrastructure.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import kr.hhplus.be.server.domain.statistics.PopularProductsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface OrderStatisticsJpaRepository extends JpaRepository<OrderStatistics, Long> {

    @Query("""
        select new kr.hhplus.be.server.domain.statistics.PopularProductsProjection(
            os.product.id,
            os.product.title,
            os.product.price,
            sum(os.totalSoldQuantity)
        )
        from OrderStatistics os
        where os.statisticsDate between :startDate and :endDate
        group by os.product.id
        order by sum(os.totalSoldQuantity) desc
    """)
    Page<PopularProductsProjection> findPopularProductsForDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);



    @Query("""
        select os
        from OrderStatistics os
        where os.statisticsDate >= :startDate and os.statisticsDate <= :endDate
          and os.product.id in :productIds
    """)
    List<OrderStatistics> findProductIdsAndDate(LocalDate startDate, LocalDate endDate, Set<Long> productIds);


    @Query("""
        select os.product.id
        from OrderStatistics os
        where os.statisticsDate between :startDate and :endDate
        group by os.product.id
        order by sum(os.totalSoldQuantity) desc
    """)
    Page<Long> findPopularProductIdsForDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
