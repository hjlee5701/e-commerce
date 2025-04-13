package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.order.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    @InjectMocks
    PaymentService paymentService;


    @Test
    @DisplayName("쿠폰 없는 결제 처리의 생성부터 저장까지 전체 흐름 테스트")
    void 쿠폰_없는_결제_정상_흐름_테스트() {
        // given
        Order order = mock(Order.class);
        Member member = mock(Member.class);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        given(paymentRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        given(order.getMember()).willReturn(member);
        given(order.getTotalAmount()).willReturn(BigDecimal.TEN);

        // when
        Payment savedPayment = paymentService.pay(order, null, member);

        // then
        verify(paymentRepository).save(captor.capture());
        assertEquals(savedPayment, captor.getValue());
    }

    @Test
    @DisplayName("쿠폰 있는 결제 처리의 생성부터 저장까지 전체 흐름 테스트")
    void 쿠폰_있는_결제_정상_흐름_테스트() {
        // given
        Order order = mock(Order.class);
        CouponItem couponItem = mock(CouponItem.class);
        Member member = mock(Member.class);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        given(paymentRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        given(order.getMember()).willReturn(member);
        given(order.getTotalAmount()).willReturn(BigDecimal.TEN);
        given(couponItem.apply(order.getOrderedAt())).willReturn(BigDecimal.TEN);

        // when
        Payment savedPayment = paymentService.pay(order, couponItem, member);

        // then
        verify(paymentRepository).save(captor.capture());
        assertEquals(savedPayment, captor.getValue());
    }


}
