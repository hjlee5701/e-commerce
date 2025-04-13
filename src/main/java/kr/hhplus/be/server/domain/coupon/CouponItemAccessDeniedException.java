package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;

public class CouponItemAccessDeniedException extends ECommerceException {
    public CouponItemAccessDeniedException() {
        super(CouponErrorCode.COUPON_ITEM_ACCESS_DENIED);
    }
}
