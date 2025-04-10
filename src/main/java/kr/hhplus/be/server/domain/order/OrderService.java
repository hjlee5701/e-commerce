package kr.hhplus.be.server.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderInfo.Created create(OrderCommand.Create command) {

        // 주문 저장
        Order order = orderRepository.save(OrderFactory.create(command));

        // 주문 상품 저장
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderCommand.ItemCreate> itemsCommands = command.getOrderItems();

        for (OrderCommand.ItemCreate itemCommand : itemsCommands) {

            orderItems.add(OrderFactory.createItem(order, itemCommand));
        }
        orderItemRepository.saveAll(orderItems);
        return OrderInfo.Created.of(order, orderItems);
    }



}
