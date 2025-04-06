package kr.hhplus.be.server.domain.memberPoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Schema(title = "사용자 금액 충전 응답")
public class ChargePointResponse {
    @Schema(title = "사용자 아이디", example = "1")
    private Long memberId;

    @Schema(title = "보유 금액", example = "20000")
    private BigDecimal balance;

    @Schema(title = "충전 금액", example = "5000")
    private BigDecimal amount;
}