package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.member.Member;
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
    public static class Find {
        private Long couponItemId;
    }
    @AllArgsConstructor
    @Getter
    public static class Issue {
        private Long couponId;
        private Long memberId;

    }
}
