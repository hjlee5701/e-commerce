package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment pay(Order order, CouponItem couponItem, Member member) {

        // 결제 생성
        Payment payment = Payment.create(order, member);

        // 쿠폰 적용
        payment.applyCoupon(couponItem);

        // 최종 결제 금액 계산
        payment.calculateFinalAmount();

        return paymentRepository.save(payment);
    }
}
