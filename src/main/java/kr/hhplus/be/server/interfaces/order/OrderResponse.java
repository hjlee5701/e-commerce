package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
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
        private List<OrderItemResponse.Item> orderItems;
    }

}
