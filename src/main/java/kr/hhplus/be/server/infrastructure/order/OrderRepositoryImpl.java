package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {
    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return Optional.empty();
    }
}
