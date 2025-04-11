package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment pay(Order order, CouponItem couponItem) {

        BigDecimal originalAmount = order.getTotalAmount();
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        // 쿠폰 적용
        if (couponItem != null) {
            discountAmount = couponItem.apply(order.getOrderedAt());
        }

        // 최종 주문 결제 금액
        BigDecimal finalAmount = originalAmount.subtract(discountAmount).max(BigDecimal.ZERO);

        Payment payment = new Payment(
                null, order, originalAmount, discountAmount, finalAmount, PaymentStatus.PENDING
        );

        return paymentRepository.save(payment);
    }
}
