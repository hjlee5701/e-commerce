package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberFixture;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER;
import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CouponItemTest {



    @Test
    @DisplayName("Coupon 의 날짜 정책 검증에 실패하면 예외가 발생한다.")
    void 쿠폰_정책이_유효하지_않으면_예외() {
        Coupon coupon = mock(Coupon.class);
        doThrow(new CouponExpiredException(FIXED_NOW)).when(coupon).validateTime(FIXED_NOW);

        CouponItem item = new CouponItemFixture().createUsable(coupon);

        assertThatThrownBy(() -> item.apply(FIXED_NOW))
                .isInstanceOf(CouponExpiredException.class);
    }


    @Test
    @DisplayName("쿠폰 상태가 USABLE 일 때 할인 적용에 성공한다.")
    void 쿠폰_사용_가능_상태일때_정상적으로_할인_적용() {
        // given
        Coupon coupon = mock(Coupon.class);;
        when(coupon.getDiscountAmount()).thenReturn(BigDecimal.TEN);
        CouponItem couponItem = new CouponItemFixture().createUsable(coupon);

        LocalDateTime orderedAt = FIXED_NOW;
        // when
        BigDecimal discount = couponItem.apply(orderedAt);

        // then
        assertThat(discount).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(couponItem.getStatus()).isEqualTo(CouponItemStatus.USED);
        verify(coupon).validateTime(orderedAt);
    }


    @ParameterizedTest(name = "쿠폰 사용 처리 중, 상태가 {0} 인 경우에 예외가 발생한다.")
    @ValueSource(strings = {"USED", "EXPIRED"})
    void 쿠폰_아이템_상태가_USABLE_아니면_예외(String statusName) {
        // given
        CouponItemStatus status = CouponItemStatus.valueOf(statusName);
        Coupon coupon = mock(Coupon.class);
        CouponItem couponItem = new CouponItemFixture().createUnUsable(coupon, status);

        // when & then
        assertThatThrownBy(() -> couponItem.apply(FIXED_NOW))
                .isInstanceOf(UnUsableCouponItemException.class)
                .hasMessageContaining(CouponErrorCode.UNUSABLE_COUPON_ITEM.getMessage());
    }

    @DisplayName("쿠폰 아이템 소유자가 아닌 경우 예외 발생")
    @Test
    void 쿠폰_아이템_소유자가_아닌경우_예외() {
        // given
        Member memberA = new Member(1L, "testerA", FIXED_NOW);
        Member memberB = new Member(2L, "testerB", FIXED_NOW);
        CouponItem couponItem = new CouponItemFixture().createWithOwner(memberA);

        // when & then
        assertThatThrownBy(() -> couponItem.checkOwner(memberB))
                .isInstanceOf(CouponItemAccessDeniedException.class)
                .hasMessageContaining(CouponErrorCode.COUPON_ITEM_ACCESS_DENIED.getMessage());
    }

}
