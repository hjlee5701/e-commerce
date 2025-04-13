package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderFixture;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    @InjectMocks
    PaymentService paymentService;

    @DisplayName("쿠폰이 없다면 할인 없이 결제가 진행된다.")
    @Test
    void 쿠폰_없는_결제() {
        // given
        BigDecimal originalAmount = BigDecimal.valueOf(5000);
        Order order = mock(Order.class);
        given(order.getTotalAmount()).willReturn(originalAmount);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        given(paymentRepository.save(any())).willAnswer(inv -> inv.getArgument(0)); // 그대로 리턴

        // when
        Payment savedPayment = paymentService.pay(order, null);

        // then
        assertThat(savedPayment.getOriginalAmount()).isEqualByComparingTo(originalAmount);
        assertThat(savedPayment.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(savedPayment.getFinalAmount()).isEqualByComparingTo(originalAmount);
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);

        verify(paymentRepository).save(captor.capture());
        verify(order).getTotalAmount();
    }


    @Test
    @DisplayName("쿠폰이 있다면 할인이 적용된 결제 금액이 저장된다.")
    void 쿠폰이_존재하면_할인이_적용된_결제가_저장() {
        // given
        BigDecimal originalAmount = BigDecimal.valueOf(5000);
        BigDecimal discountAmount = BigDecimal.valueOf(3000);
        LocalDateTime orderedAt = FIXED_NOW;

        Order order = mock(Order.class);
        CouponItem couponItem = mock(CouponItem.class);

        given(order.getTotalAmount()).willReturn(originalAmount);
        given(order.getTotalAmount()).willReturn(originalAmount);
        given(order.getOrderedAt()).willReturn(orderedAt);
        given(couponItem.apply(orderedAt)).willReturn(discountAmount);
        given(paymentRepository.save(any())).willAnswer(inv -> inv.getArgument(0)); // 그대로 리턴

        // when
        Payment result = paymentService.pay(order, couponItem);

        // then
        assertEquals(originalAmount.subtract(discountAmount), result.getFinalAmount());
        verify(order).getTotalAmount();
        verify(couponItem).apply(orderedAt);
    }

    @Test
    @DisplayName("할인 금액에 총 주문 금액보다 크면 최종 결제 금액은 0 원이다.")
    void 할인금액이_원금보다_크면_최종금액은_0원() {
        // given
        BigDecimal originalAmount = BigDecimal.valueOf(5000);
        BigDecimal discountAmount = BigDecimal.valueOf(10000);
        LocalDateTime orderedAt = FIXED_NOW;

        Order order = mock(Order.class);
        CouponItem couponItem = mock(CouponItem.class);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        given(order.getTotalAmount()).willReturn(originalAmount);
        given(order.getOrderedAt()).willReturn(orderedAt);
        given(couponItem.apply(orderedAt)).willReturn(discountAmount);
        given(paymentRepository.save(any())).willAnswer(inv -> inv.getArgument(0)); // 그대로 리턴

        // when
        Payment payment = paymentService.pay(order, couponItem);

        // then
        verify(paymentRepository).save(captor.capture());
        assertThat(payment.getDiscountAmount()).isEqualByComparingTo(discountAmount);
        assertThat(payment.getFinalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }


}
