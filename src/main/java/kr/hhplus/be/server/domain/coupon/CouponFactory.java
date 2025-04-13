package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.member.Member;

public class CouponFactory {

    public static CouponItem issueCouponItem(Long memberId, Long couponId) {
        return new CouponItem(null, Member.referenceById(memberId), Coupon.referenceById(couponId), CouponItemStatus.USABLE);
    }
}
