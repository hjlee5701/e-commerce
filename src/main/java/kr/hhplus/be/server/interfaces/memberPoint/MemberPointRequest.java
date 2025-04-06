package kr.hhplus.be.server.interfaces.memberPoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Schema(title = "사용자 금액 요청")
public class MemberPointRequest {

    @AllArgsConstructor
    @Getter
    @Schema(title = "사용자 금액 충전 요청")
    public static class Charge {
        @Schema(title = "충전 금액", example = "5000")
        private BigDecimal amount;
    }

}
