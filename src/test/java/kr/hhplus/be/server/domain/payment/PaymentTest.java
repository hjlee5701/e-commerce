package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderFixture;
import kr.hhplus.be.server.shared.code.PaymentErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PaymentTest {

    @Test
    @DisplayName("결제 생성시 Order 가 Null 로 NPE 발생한다.")
    void 결제_생성시_주문_널값으로_NPE_발생() {
        // given
        Long memberId = ANY_MEMBER_ID;
        // when & then
        assertThatThrownBy(() -> Payment.create(null, memberId))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("결제 생성시 Member 가 Null 로 NPE 발생한다.")
    void 결제_생성시_회원_널값으로_NPE_발생() {
        // given
        Order order = new OrderFixture().create();

        // when & then
        assertThatThrownBy(() -> Payment.create(order, null))
                .isInstanceOf(NullPointerException.class);

    }


    @Test
    @DisplayName("결제 생성시, 요청한 사용자와 주문 생성한 사용자의 불일치로 예외가 발생한다.")
    void 결제_생성시_주문_결제자와_결제_요청자의_불일치() {
        // given
        Order order = new OrderFixture().withMemberId(1L);
        Long forbiddenMemberId = 99L;

        assertThatThrownBy(() -> Payment.create(order, forbiddenMemberId))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(PaymentErrorCode.UNMATCHED_ORDER_MEMBER.getMessage());
    }

    @Test
    @DisplayName("결제 중 쿠폰이 Null 이면 할인 금액은 0원이다.")
    void 쿠폰이_null_이면_할인금액_0원() {
        // given
        Payment payment = new PaymentFixture().create();

        // when
        payment.applyCoupon(null);

        // then
        assertEquals(payment.getDiscountAmount(), BigDecimal.ZERO);
    }

    @Test
    @DisplayName("결제 쿠폰 적용시 couponItem.apply(orderedAt) 이 호출된다")
    void 결제_쿠폰_적용시_couponItem_apply_호출() {
        // given
        Payment payment = new PaymentFixture().create();
        CouponItem couponItem = mock(CouponItem.class);

        // when
        payment.applyCoupon(couponItem);

        // then
        verify(couponItem).apply(payment.getOrder().getOrderedAt());
    }


    @Test
    @DisplayName("할인 금액에 총 주문 금액보다 크면 최종 결제 금액은 0 원이다.")
    void 할인금액이_원금보다_크면_최종금액은_0원() {
        // given
        BigDecimal originalAmount = BigDecimal.valueOf(5000);
        BigDecimal discountAmount = BigDecimal.valueOf(10000);
        Payment payment = new PaymentFixture().withAmount(originalAmount, discountAmount);

        // when
        payment.calculateFinalAmount();

        // then
        assertEquals(payment.getFinalAmount(), BigDecimal.ZERO);
    }


    @ParameterizedTest
    @ValueSource(strings = {"COMPLETED", "CANCELED", "REFUNDED", "FAILED"})
    void 결제_완료_처리중_PENDING_상태가_아니면_예외가_발생한다(String statusName) {
        // given
        PaymentStatus status = PaymentStatus.valueOf(statusName);
        Payment payment = new PaymentFixture().withOrderAndStatus(mock(Order.class), status);

        // when & then
        assertThatThrownBy(payment::markAsPaid)
                .isInstanceOf(ECommerceException.class)
                .hasMessageContaining(status.name());
    }

    @Test
    @DisplayName("markAsPaid 호출 시 주문의 applyPayment(finalAmount) 가 호출된다")
    void markAsPaid_호출시_applyPayment_함께_호출() {
        // given
        BigDecimal finalAmount = BigDecimal.valueOf(200L);
        Order order = mock(Order.class);

        Payment payment = new PaymentFixture().withFinalAmountAndOrder(finalAmount, order);

        // when
        payment.markAsPaid();

        // then
        verify(order).applyPayment(payment.getFinalAmount());
    }

    @Test
    @DisplayName("결제 상태가 PENDING이면 COMPELTED로 변경되고 주문 상태도 도 확정된다")
    void 결제완료로_변경_후_주문_확정() {
        // given
        Order mockOrder = mock(Order.class); // 주문은 테스트하지 않음
        Payment payment = new PaymentFixture().withOrderAndStatus(mockOrder,PaymentStatus.PENDING);

        // when
        payment.markAsPaid();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

}
