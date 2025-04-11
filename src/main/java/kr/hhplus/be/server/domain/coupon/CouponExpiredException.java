package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;

import java.time.LocalDateTime;

public class CouponExpiredException extends ECommerceException {
    public CouponExpiredException(LocalDateTime expiredAt) {
        super(CouponErrorCode.COUPON_EXPIRED, expiredAt);

    }
}
