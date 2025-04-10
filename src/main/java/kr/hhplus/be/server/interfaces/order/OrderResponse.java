package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.OrderResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(title = "주문 정보 응답")
public class OrderResponse {

    @Getter
    @AllArgsConstructor
    @Schema(title = "상품 주문 생성 응답")
    public static class Created {
        @Schema(title = "주문 아이디", example = "1")
        private Long orderId;

        @Schema(title = "주문 상태", example = "COMPLETED", description = "주문 진행 상태")
        private String orderStatus;

        @Schema(title = "총 주문 금액", example = "40000", description = "주문 상품 가격의 합")
        private BigDecimal totalAmount;

        @Schema(title = "주문 날짜", example = "2025-04-04T10:00:00", description = "주문이 생성된 시간")
        private LocalDateTime orderAt;

        @Schema(title = "주문 상품 목록", description = "주문한 상품 리스트")
        private List<ItemCreated> orderItems;

        public static Created of(OrderResult.Created result) {
            List<ItemCreated> orderItems = result.getOrderItems().stream()
                    .map(item -> new ItemCreated(
                            item.getProductId(), item.getTitle(), item.getPrice(), item.getQuantity()
                    ))
                    .toList();
            return new Created(result.getOrderId(), result.getOrderStatus(), result.getTotalAmount(), null, orderItems);
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(title = "주문한 상품 정보 반환")
    public static class ItemCreated {
        @Schema(title = "상품 아이디", example = "1")
        private Long productId;

        @Schema(title = "상품명", example = "상의")
        private String title;

        @Schema(title = "상품 가격", example = "10000")
        private BigDecimal price;

        @Schema(title = "주문 수량", example = "1")
        private Integer quantity;

    }

}
