package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;

public class CouponHasNoRemainingException extends ECommerceException {
    public CouponHasNoRemainingException() {
        super(CouponErrorCode.COUPON_NO_REMAINING);
    }
}
