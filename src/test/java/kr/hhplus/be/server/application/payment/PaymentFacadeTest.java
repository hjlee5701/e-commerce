package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeTest {

    @Mock
    private OrderService orderService;

    @Mock
    private MemberService memberService;

    @Mock
    private CouponService couponService;

    @Mock
    private MemberPointService memberPointService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    PaymentFacade facade;

    @Test
    @DisplayName("결제 생성 시 서비스 호출 순서 검증한다.")
    void 서비스_호출_순서_검증() {
        // given
        Order order = mock(Order.class);
        CouponItem couponItem = mock(CouponItem.class);
        Payment payment = mock(Payment.class);
        Member member = mock(Member.class);

        PaymentCriteria.Pay criteria = mock(PaymentCriteria.Pay.class);

        given(order.getMember()).willReturn(member);
        given(memberService.findMemberById(any())).willReturn(mock(MemberInfo.Detail.class));
        given(orderService.findByOrderId(any())).willReturn(order);
        given(couponService.findByCouponItemId(any())).willReturn(couponItem);
        given(paymentService.pay(any())).willReturn(payment);
        given(payment.getStatus()).willReturn(PaymentStatus.PENDING);

        // when
        facade.createPayment(criteria);

        // then
        InOrder inOrder = inOrder(
                memberService, orderService, couponService, paymentService, memberPointService, payment
        );

        inOrder.verify(memberService).findMemberById(any());
        inOrder.verify(orderService).findByOrderId(any());
        inOrder.verify(couponService).findByCouponItemId(any());
        inOrder.verify(paymentService).pay(any());
        inOrder.verify(memberPointService).use(any());
        inOrder.verify(payment).markAsPaid();
    }
}
