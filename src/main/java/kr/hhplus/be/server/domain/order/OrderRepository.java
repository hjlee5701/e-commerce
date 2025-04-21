package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long orderId);

    List<Order> getPaidOrderByDate(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status);
}
