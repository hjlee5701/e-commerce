package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderInfo {

    @Getter
    @AllArgsConstructor
    public static class Created {
        private Long orderId;
        private String orderStatus;
        private BigDecimal totalAmount;
        private LocalDateTime orderedAt;
        private List<ItemCreated> items;

        public static Created of(Order order, List<OrderItem> orderItems) {
            List<ItemCreated> itemInfos = orderItems.stream()
                    .map(orderItem -> new ItemCreated(
                            orderItem.getId(),
                            orderItem.getTitle(),
                            orderItem.getUnitPrice(),
                            orderItem.getTotalPrice(),
                            orderItem.getQuantity()
                    ))
                    .toList();
            return new Created(
                    order.getId(),
                    order.getStatus().name(),
                    order.getTotalAmount(),
                    order.getOrderedAt(),
                    itemInfos
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ItemCreated {
        private Long orderItemId;
        private String title;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private Integer quantity;
    }

    @Getter
    @AllArgsConstructor
    public static class Paid {
        private Long productId;
        private Integer orderQuantity;
        public static Paid of(Product product, OrderItem orderItem) {
            return new Paid(product.getId(), orderItem.getQuantity());
        }
    }
}
