package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CouponTest {

    @Test
    @DisplayName("유효 기간 지났거나 만료 상태라면 예외가 발생한다.")
    void 유효기간을_지났거나_만료상태면_예외발생() {
        Coupon coupon = new CouponFixture().createWithExpired();
        assertThatThrownBy(() -> coupon.validateTime(FIXED_NOW))
                .isInstanceOf(CouponExpiredException.class)
                .hasMessage(String.format(CouponErrorCode.COUPON_EXPIRED.getMessage(), coupon.expiredAt));
    }

    @Test
    @DisplayName("쿠폰 상태는 ACTIVE 지만 만료일 이후라면 예외가 발생한다.")
    void 쿠폰상태는_ACTIVE지만_만료일_이후라면_예외발생() {
        Coupon coupon = new CouponFixture().createWithExpiredAt(CouponStatus.ACTIVE);

        assertThatThrownBy(() -> coupon.validateTime(FIXED_NOW))
                .isInstanceOf(CouponExpiredException.class)
                .hasMessage(String.format(CouponErrorCode.COUPON_EXPIRED.getMessage(), coupon.expiredAt));
    }

    @Test
    @DisplayName("쿠폰 상태는 INACTIV 이거나 발급일(사용 가능일) 이후라면 예외가 발생한다.")
    void 쿠폰상태가_INACTIVE거나_발급일보다_이전에_사용하면_예외() {
        Coupon coupon = new CouponFixture().createWithInActive();

        assertThatThrownBy(() -> coupon.validateTime(FIXED_NOW))
                .isInstanceOf(CouponNotYetActiveException.class)
                .hasMessage(String.format(CouponErrorCode.COUPON_NOT_YET_ACTIVE.getMessage(), coupon.issuedAt));
    }

    @Test
    @DisplayName("유효한 쿠폰이면 예외 없이 통과된다.")
    void 유효한_쿠폰이면_예외없이_통과() {
        Coupon coupon = new CouponFixture().createWithStatus(CouponStatus.ACTIVE);

        assertThatCode(() -> coupon.validateTime(FIXED_NOW)).doesNotThrowAnyException();
    }

}
