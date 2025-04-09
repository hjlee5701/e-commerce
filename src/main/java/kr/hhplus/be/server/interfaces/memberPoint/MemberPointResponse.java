package kr.hhplus.be.server.interfaces.memberPoint;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.memberPoint.MemberPointResult;
import kr.hhplus.be.server.domain.memberPoint.MemberPointInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Schema(title = "사용자 금액 응답")
public class MemberPointResponse {

    @Getter
    @AllArgsConstructor
    @Schema(title = "사용자 보유 금액 응답")
    public static class ChargeBalance {
        @Schema(title = "사용자 아이디", example = "1")
        private Long memberId;

        @Schema(title = "보유 금액", example = "20000")
        private BigDecimal balance;

        public static ChargeBalance of(MemberPointResult.ChargeBalance result) {
            return new ChargeBalance(result.getMemberId(), result.getBalance());
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(title = "사용자 보유 금액 조회 응답")
    public static class Balance {
        @Schema(title = "보유 금액", example = "20000")
        private BigDecimal balance;

        public static Balance of(MemberPointInfo.Balance result) {
            return new Balance(result.getBalance());
        }
    }
}
