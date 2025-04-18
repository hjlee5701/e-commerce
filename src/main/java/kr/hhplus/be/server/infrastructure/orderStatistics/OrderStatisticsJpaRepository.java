package kr.hhplus.be.server.infrastructure.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import kr.hhplus.be.server.domain.statistics.PopularProductsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderStatisticsJpaRepository extends JpaRepository<OrderStatistics, Long> {
    @Query("select new kr.hhplus.be.server.domain.statistics.PopularProductsProjection(" +
            "      os.product.id," +
            "      os.product.title," +
            "      os.product.price," +
            "      sum(os.totalSoldQuantity)" +
            ") " +
            " from OrderStatistics os " +
            "where os.statisticsAt >= :startDate and os.statisticsAt <= :endDate " +
            "group by os.product.id " +
            "order by sum(os.totalSoldQuantity) desc")
    Page<PopularProductsProjection> findPopularProductsForDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

}
