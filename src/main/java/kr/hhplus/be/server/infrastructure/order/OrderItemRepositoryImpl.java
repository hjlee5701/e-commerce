package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemRepositoryImpl implements OrderItemRepository {
    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItem) {
        return null;
    }
}
