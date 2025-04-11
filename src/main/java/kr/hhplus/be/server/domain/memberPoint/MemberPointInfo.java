package kr.hhplus.be.server.domain.memberPoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class MemberPointInfo {

    @Getter
    @AllArgsConstructor
    public static class Balance{
        private BigDecimal balance;

        public static Balance of(MemberPoint memberPoint) {
            return new Balance(memberPoint.getBalance());
        }
    }
}
