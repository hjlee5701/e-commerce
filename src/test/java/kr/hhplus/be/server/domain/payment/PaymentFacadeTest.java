package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderService;
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
        given(payment.getStatus()).willReturn(PaymentStatus.PENDING);

        PaymentCriteria.Pay criteria = mock(PaymentCriteria.Pay.class);

        // 스텁 정의
        given(criteria.toFindOrderCommand()).willReturn(new OrderCommand.Find(1L)); // dummy
        given(criteria.getCouponItemId()).willReturn(99L);
        given(criteria.toFindCouponItem()).willReturn(new CouponCommand.Find(1L));
        given(criteria.toUseMemberPointCommand(payment, order)).willReturn(mock(MemberPointCommand.Use.class));

        given(orderService.findByOrderId(any())).willReturn(order);
        given(couponService.findByCouponItemId(any())).willReturn(couponItem);
        given(paymentService.pay(order, couponItem)).willReturn(payment);

        // when
        facade.createPayment(criteria);

        // then
        InOrder inOrder = inOrder(
                orderService, couponService, paymentService, memberPointService, payment
        );

        inOrder.verify(orderService).findByOrderId(any());
        inOrder.verify(couponService).findByCouponItemId(any());
        inOrder.verify(paymentService).pay(order, couponItem);
        inOrder.verify(memberPointService).use(any());
        inOrder.verify(payment).markAsPaid();
    }
}
