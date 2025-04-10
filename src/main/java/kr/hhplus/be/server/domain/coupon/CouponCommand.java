package kr.hhplus.be.server.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CouponCommand {

    @AllArgsConstructor
    @Getter
    public static class Holdings {
        private Long memberId;
    }
    @AllArgsConstructor
    @Getter
    public static class UsableCoupon {
        private Long couponItemId;
    }
}
