package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import kr.hhplus.be.server.interfaces.code.ErrorCode;

public class UnUsableCouponItemException extends ECommerceException {
    public UnUsableCouponItemException() {
        super(CouponErrorCode.UNUSABLE_COUPON_ITEM);
    }
}
