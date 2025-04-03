package kr.hhplus.be.server.domain.order.dto;

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

    @Schema(title = "주문 아이디", example = "1")
    private Long orderId;

    @Schema(title = "주문 상태", example = "COMPLETED", description = "주문 진행 상태")
    private String orderStatus;

    @Schema(title = "주문 날짜", example = "2025-04-04T10:00:00", description = "주문이 생성된 시간")
    private LocalDateTime orderAt;

    @Schema(title = "할인 전 금액", example = "50000", description = "할인전 상품별 총계")
    private BigDecimal originalAmount;

    @Schema(title = "할인 금액", example = "10000", description = "쿠폰 할인 금액")
    private BigDecimal discountAmount;

    @Schema(title = "쿠폰 상태", example = "USED", description = "(ISSUED, USED)")
    private String couponStatus;

    @Schema(title = "주문 상품 목록", description = "주문한 상품 리스트")
    private List<OrderItemResponse> orderItems;

    @Schema(title = "결제 정보")
    private PaymentResponse payment;
}
