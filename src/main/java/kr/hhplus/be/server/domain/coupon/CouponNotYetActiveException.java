package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;

import java.time.LocalDateTime;

public class CouponNotYetActiveException extends ECommerceException {
    public CouponNotYetActiveException(LocalDateTime issuedAt) {
        super(CouponErrorCode.COUPON_NOT_YET_ACTIVE, issuedAt);
    }
}
