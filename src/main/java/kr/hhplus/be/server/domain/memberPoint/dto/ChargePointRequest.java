package kr.hhplus.be.server.domain.memberPoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Schema(title = "사용자 금액 충전 요청")
public class ChargePointRequest {

    @Schema(title = "충전 금액", example = "5000")
    private BigDecimal amount;

}
