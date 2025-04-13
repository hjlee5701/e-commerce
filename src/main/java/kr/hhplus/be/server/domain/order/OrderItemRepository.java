package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository {
    List<OrderItem> saveAll(List<OrderItem> orderItem);
}
