package kr.hhplus.be.server.application.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PaymentCriteria {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long orderId;
    }
}
