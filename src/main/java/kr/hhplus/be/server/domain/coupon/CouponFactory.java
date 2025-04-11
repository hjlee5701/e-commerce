package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.member.Member;

public class CouponFactory {

    public static CouponItem issueCouponItem(Member member, Coupon coupon) {
        return new CouponItem(null, member, coupon, CouponItemStatus.USABLE);
    }
}
