package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.*;

public class CouponTest {

    @Test
    @DisplayName("유효 기간 지났거나 만료 상태라면 예외가 발생한다.")
    void 유효기간을_지났거나_만료상태면_예외발생() {
        Coupon coupon = new CouponFixture().withExpired();
        assertThatThrownBy(() -> coupon.validateTime(FIXED_NOW))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(String.format(CouponErrorCode.COUPON_EXPIRED.getMessage(), coupon.expiredAt));
    }

    @Test
    @DisplayName("쿠폰 상태는 ACTIVE 지만 만료일 이후라면 예외가 발생한다.")
    void 쿠폰상태는_ACTIVE지만_만료일_이후라면_예외발생() {
        Coupon coupon = new CouponFixture().withExpiredAt(CouponStatus.ACTIVE);

        assertThatThrownBy(() -> coupon.validateTime(FIXED_NOW))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(String.format(CouponErrorCode.COUPON_EXPIRED.getMessage(), coupon.expiredAt));
    }

    @Test
    @DisplayName("쿠폰 상태는 INACTIV 이거나 발급일(사용 가능일) 이후라면 예외가 발생한다.")
    void 쿠폰상태가_INACTIVE거나_발급일보다_이전에_사용하면_예외() {
        Coupon coupon = new CouponFixture().withInActive();

        assertThatThrownBy(() -> coupon.validateTime(FIXED_NOW))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(String.format(CouponErrorCode.COUPON_NOT_YET_ACTIVE.getMessage(), coupon.issuedAt));
    }

    @Test
    @DisplayName("유효한 쿠폰이면 예외 없이 통과된다.")
    void 유효한_쿠폰이면_예외없이_통과() {
        Coupon coupon = new CouponFixture().withStatus(CouponStatus.ACTIVE);

        assertThatCode(() -> coupon.validateTime(FIXED_NOW)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("쿠폰 정상 발급시 수량이 감소한다.")
    void 쿠폰_정상_발급시_수량_감소() {
        // given
        Coupon coupon = new CouponFixture().create();
        int beforeIssuedQuantity = coupon.getRemainingQuantity();

        // when
        coupon.issue(FIXED_NOW, ANY_MEMBER_ID);

        // then
        assertThat(coupon.getRemainingQuantity()).isEqualTo(beforeIssuedQuantity-1);
    }
    @Test
    @DisplayName("쿠폰 정상 발급시 사용 가능한 쿠폰 아이템이 반환된다.")
    void 쿠폰_정상_발급시_쿠폰_아이템이_반환() {
        // given
        Coupon coupon = new CouponFixture().create();

        // when
        CouponItem couponItem = coupon.issue(FIXED_NOW, ANY_MEMBER_ID);

        // then
        assertThat(CouponItemStatus.USABLE).isEqualTo(couponItem.getStatus());
    }

    @Test
    @DisplayName("쿠폰 상태가 ACITVE 가 아니면 발급시 예외 발생한다.")
    void 쿠폰_상태가_ACTIVE가_아니면_예외_발생() {
        // given
        Coupon coupon = new CouponFixture().withInActive();

        // when & then
        assertThatThrownBy(() -> coupon.issue(FIXED_NOW, ANY_MEMBER_ID))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(CouponErrorCode.COUPON_INACTIVE.getMessage())
        ;
    }

    @Test
    @DisplayName("발급일 기준 쿠폰 만료일 후라면, 예외 발생한다.")
    void 발급일이_쿠폰_만료일_후라면_예외() {
        // given
        Coupon coupon = new CouponFixture().withExpiredAt(CouponStatus.ACTIVE);

        // when & then
        assertThatThrownBy(() -> coupon.issue(FIXED_NOW, ANY_MEMBER_ID))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(String.format(CouponErrorCode.COUPON_EXPIRED.getMessage(), coupon.expiredAt));
        ;
    }

    @Test
    @DisplayName("쿠폰 수량이 0 이하인 경우 예외가 발생한다.")
    void 쿠폰_수량이_0이하면_예외가_발생한다() {
        // given
        Coupon coupon = new CouponFixture().withNoRemaining();
        // when & then
        assertThatThrownBy(() -> coupon.issue(FIXED_NOW, ANY_MEMBER_ID))
                .isInstanceOf(ECommerceException.class);
    }



}
