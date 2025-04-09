package kr.hhplus.be.server.application.memberPoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class MemberPointResult {
    @Getter
    @AllArgsConstructor
    public static class ChargeBalance {
        private Long memberId;
        private BigDecimal balance;
    }

    @Getter
    @AllArgsConstructor
    public static class Balance {
        private BigDecimal balance;
    }
}
