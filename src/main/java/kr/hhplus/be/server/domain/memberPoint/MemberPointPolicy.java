package kr.hhplus.be.server.domain.memberPoint;

import java.math.BigDecimal;

public class MemberPointPolicy {
    public static final BigDecimal MAX_CHARGE_AMOUNT = BigDecimal.valueOf(100_000_000);
    public static final BigDecimal MAX_BALANCE_AMOUNT = BigDecimal.valueOf(100_000_000);
    public static final BigDecimal MIN_CHARGE_AMOUNT = BigDecimal.valueOf(1);
    public static final int MIN_BALANCE_AMOUNT = 0;
}
