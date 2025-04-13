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
        private List<ItemDetail> items;

        public static Created of(Order order, List<OrderItem> orderItems) {
            List<ItemDetail> itemInfos = orderItems.stream()
                    .map(orderItem -> new ItemDetail(
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
    public static class ItemDetail {
        private Long orderItemId;
        private String title;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private Integer quantity;
    }

    @Getter
    @AllArgsConstructor
    public static class Detail {
        private Long orderId;
        private Long orderMemberId;
        private List<ItemDetail> orderItems;
        private OrderStatus status;
        private LocalDateTime orderAt;

        public static Detail of(Order order) {
            List<ItemDetail> itemInfos = order.getOrderItems().stream()
                    .map(orderItem -> new ItemDetail(
                            orderItem.getId(),
                            orderItem.getTitle(),
                            orderItem.getUnitPrice(),
                            orderItem.getTotalPrice(),
                            orderItem.getQuantity()
                    ))
                    .toList();
            return new Detail(order.getId(), order.getMember().getId(), itemInfos, order.getStatus(), order.getOrderedAt());
        }
    }
}
