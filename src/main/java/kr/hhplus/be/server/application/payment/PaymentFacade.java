package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentFacade {

    private final OrderService orderService;
    private final MemberService memberService;
    private final CouponService couponService;
    private final PaymentService paymentService;
    private final MemberPointService memberPointService;
    private final ProductStockService productStockService;

    @Transactional
    public PaymentResult.Paid createPayment(PaymentCriteria.Pay criteria) {
        // 결제 요청자 조회
        MemberInfo.Detail memberInfo = memberService.findMemberById(criteria.toFindMemberCommand());

        // 주문 조회
        Order order = orderService.findByOrderId(criteria.toFindOrderCommand());
        order.checkOrderer(memberInfo.getMemberId());

        CouponItem couponItem = null;
        if (criteria.getCouponItemId() != null) {
            couponItem = couponService.findByCouponItemId(criteria.toFindCouponItem());
            couponItem.checkOwner(order.getMember().getId());
        }

        // 결제 생성
        Payment payment = paymentService.pay(criteria.toPayCommand(order, couponItem));

        // 재고 감소
        productStockService.decreaseStock(criteria.toDecreaseStockCommand(order.getOrderItems()));

        // 금액 차감
        MemberPointCommand.Use command = criteria.toUseMemberPointCommand(payment, order);
        memberPointService.use(command);

        // 결제 완료
        payment.markAsPaid();

        return PaymentResult.Paid.of(payment, order.getId());

    }
}
