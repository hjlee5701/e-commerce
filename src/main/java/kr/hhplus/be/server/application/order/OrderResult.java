package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResult {

    @Getter
    @AllArgsConstructor
    public static class Created {
        private Long orderId;
        private String orderStatus;
        private BigDecimal totalAmount;
        private LocalDateTime orderedAt;
        private List<ItemCreated> orderItems;

        public static Created of(OrderInfo.Created info) {
            List<ItemCreated> items = info.getItems().stream()
                    .map(item -> new ItemCreated(
                            item.getOrderItemId(),
                            item.getTitle(),
                            item.getUnitPrice(),
                            item.getTotalPrice(),
                            item.getQuantity()
            )).toList();
            return new Created(
                    info.getOrderId(),
                    info.getOrderStatus(),
                    info.getTotalAmount(),
                    info.getOrderedAt(),
                    items
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
}
