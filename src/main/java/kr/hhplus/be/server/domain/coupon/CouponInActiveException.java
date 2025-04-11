package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;

public class CouponInActiveException extends ECommerceException {
    public CouponInActiveException() {
        super(CouponErrorCode.COUPON_INACTIVE);
    }
}
