package kr.hhplus.be.server.domain.memberPoint;

import java.math.BigDecimal;

public class MemberPointPolicy {
    public static final BigDecimal MAX_POINT_BALANCE = BigDecimal.valueOf(100_000_000);
    public static final BigDecimal MIN_POINT = BigDecimal.ZERO;
}
