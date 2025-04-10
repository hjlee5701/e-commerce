package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public class OrderResult {

    @Getter
    @AllArgsConstructor
    public static class Created {
        private Long orderId;
        private String orderStatus;
        private BigDecimal totalAmount;
        private List<ItemCreated> orderItems;

        public static Created of(OrderInfo.Created info) {
            List<ItemCreated> items = info.getItems().stream()
                    .map(item -> new ItemCreated(
                            item.getProductId(),
                            item.getTitle(),
                            item.getPrice(),
                            item.getQuantity()
            )).toList();
            return new Created(
                    info.getOrderId(),
                    info.getOrderStatus(),
                    info.getTotalAmount(),
                    items
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
