package kr.hhplus.be.server.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Completed {
        private Long orderId;
    }
}
