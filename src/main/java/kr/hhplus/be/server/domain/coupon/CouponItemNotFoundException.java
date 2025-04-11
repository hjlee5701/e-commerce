package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;

public class CouponItemNotFoundException extends ECommerceException {
    public CouponItemNotFoundException() {
        super(CouponErrorCode.COUPON_ITEM_NOT_FOUND);
    }
}
