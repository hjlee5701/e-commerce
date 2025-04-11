package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;

public class CouponNotFoundException extends ECommerceException {
    public CouponNotFoundException() {
        super(CouponErrorCode.COUPON_NOT_FOUND);
    }
}
