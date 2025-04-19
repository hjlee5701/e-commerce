package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderInfo.Created create(OrderCommand.Create command) {

        // 주문 생성
        Order order = Order.create(command.getMemberId());

        Map<Long, Integer> orderProductMap = command.getOrderProductMap();
        for (OrderCommand.ItemCreate product : command.getProducts()) {
            if (!orderProductMap.containsKey(product.getProductId())) {
                throw new ECommerceException(OrderErrorCode.INVALID_ORDER_PRODUCT);
            }
        }

        // 주문 상품 생성
        order.addItems(command.getProducts(), orderProductMap);

        // 총 가격 계산
        order.calculateTotalAmount();

        // 주문 상품 저장
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = orderItemRepository.saveAll(order.getOrderItems());

        return OrderInfo.Created.of(savedOrder, orderItems);
    }

    public Order findByOrderId(OrderCommand.Find command) {
        return orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new ECommerceException(OrderErrorCode.ORDER_NOT_FOUND));
    }


}
