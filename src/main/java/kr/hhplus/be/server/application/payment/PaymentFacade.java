package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentFacade {

    private final OrderService orderService;
    private final CouponService couponService;
    private final MemberPointService memberPointService;
    private final PaymentService paymentService;

    public PaymentResult.Paid createPayment(PaymentCriteria.Pay criteria) {
        // 주문 조회
        Order order = orderService.findByOrderId(criteria.toFindOrderCommand());

        CouponItem couponItem = null;
        if (criteria.getCouponItemId() != null) {
            couponItem = couponService.findByCouponItemId(criteria.toFindCouponItem());
        }
        // 결제 생성
        Payment payment = paymentService.pay(order, couponItem);

        // 금액 차감
        MemberPointCommand.Use command = criteria.toUseMemberPointCommand(payment, order);
        memberPointService.use(command);

        // 결제 완료
        payment.markAsPaid();

        return PaymentResult.Paid.of(payment, order.getId());

    }
}
