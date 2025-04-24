package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    @Query("""
        select o from Order o
        join fetch o.member
        join fetch o.orderItems oi
        join fetch oi.product
        where o.orderedAt >= :start and o.orderedAt <= :end
          and o.status = :status
    """)
    List<Order> findByOrderedAtBetweenAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") OrderStatus status
    );

}
