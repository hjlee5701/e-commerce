package kr.hhplus.be.server.domain.order;

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
                            orderItem.getProduct().getId(),
                            orderItem.getTitle(),
                            orderItem.getPrice(),
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
        private Long productId;
        private String title;
        private BigDecimal price;
        private Integer quantity;
    }
}
