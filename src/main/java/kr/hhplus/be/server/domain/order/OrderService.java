package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final static int PAID_ORDER_LOOKBACK_DAYS = 1;
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


    public List<OrderInfo.Paid> getPaidOrderByDate(LocalDateTime now) {
        List<OrderInfo.Paid> info = new ArrayList<>();
        LocalDateTime startDate = now.minusDays(PAID_ORDER_LOOKBACK_DAYS);
        List<Order> orders = orderRepository.getPaidOrderByDate(startDate, now, OrderStatus.PAID);
        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                Product product = orderItem.getProduct();
                info.add(OrderInfo.Paid.of(product, orderItem));
            }
        }
        return info;
    }
}
