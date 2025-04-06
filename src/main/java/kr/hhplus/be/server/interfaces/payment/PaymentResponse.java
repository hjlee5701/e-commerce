package kr.hhplus.be.server.interfaces.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Schema(title = "주문 결제 응답")
public class PaymentResponse {

    @Getter
    @AllArgsConstructor
    @Schema(title = "주문 결제 생성 응답")
    public static class Created {

        @Schema(title = "주문 아이디", example = "1")
        private Long orderId;

        @Schema(title = "결제 아이디", example = "1")
        private Long paymentId;

        @Schema(title = "할인 전 금액", example = "50000", description = "할인전 상품별 총계")
        private BigDecimal originalAmount;

        @Schema(title = "할인 금액", example = "10000", description = "쿠폰 할인 금액")
        private BigDecimal discountAmount;

        @Schema(title = "최종 금액", example = "40000", description = "총 결제 금액 (쿠폰 적용 시 할인된 총 결제 금액)")
        private BigDecimal finalAmount;

        @Schema(title = "결제 상태", example = "COMPLETED", description = "결제 진행 상태")
        private String paymentStatus;
    }
}
