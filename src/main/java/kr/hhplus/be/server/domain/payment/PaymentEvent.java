package kr.hhplus.be.server.domain.payment;

public class PaymentEvent {

    public record Completed (
            Long orderId
    ){}
}
