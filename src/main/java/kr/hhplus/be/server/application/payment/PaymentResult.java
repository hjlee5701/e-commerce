package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

public class PaymentResult {

    @Getter
    @AllArgsConstructor
    public static class Paid {
        private Long orderId;
        private Long paymentId;
        private BigDecimal originalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String paymentStatus;


        public static PaymentResult.Paid of(Payment payment, Long orderId) {
            return new PaymentResult.Paid(
                    orderId,
                    payment.getId(),
                    payment.getOriginalAmount(),
                    payment.getDiscountAmount(),
                    payment.getFinalAmount(),
                    payment.getStatus().name()
            );
        }
    }

}
