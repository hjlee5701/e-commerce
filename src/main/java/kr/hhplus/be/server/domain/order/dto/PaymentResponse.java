package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Schema(title = "결제 정보 응답")
public class PaymentResponse {

    @Schema(title = "결제 아이디", example = "1")
    private Long paymentId;

    @Schema(title = "결제 상태", example = "COMPLETED", description = "결제 진행 상태")
    private String paymentStatus;

    @Schema(title = "총 주문 금액", example = "40000", description = "할인 후 최종 결제 금액")
    private BigDecimal totalAmount;
}
