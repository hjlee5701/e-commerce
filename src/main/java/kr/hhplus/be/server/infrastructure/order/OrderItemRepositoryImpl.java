package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;
    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }
}
