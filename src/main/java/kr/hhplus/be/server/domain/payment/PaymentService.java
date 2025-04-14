package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment pay(PaymentCommand.Pay command) {

        // 결제 생성
        Payment payment = Payment.create(command.getOrder(), command.getMemberId());

        // 쿠폰 적용
        payment.applyCoupon(command.getCouponItem());

        // 최종 결제 금액 계산
        payment.calculateFinalAmount();

        return paymentRepository.save(payment);
    }
}
