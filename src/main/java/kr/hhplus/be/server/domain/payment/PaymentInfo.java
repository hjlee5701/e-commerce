package kr.hhplus.be.server.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class PaymentInfo {

    @Getter
    @AllArgsConstructor
    public static class Paid {
        private Long orderId;
        private Long paymentId;
        private BigDecimal originalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String paymentStatus;


        public static Paid of(Payment payment, Long orderId) {
            return new Paid(
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
