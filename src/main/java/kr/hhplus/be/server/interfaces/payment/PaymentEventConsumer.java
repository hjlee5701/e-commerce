package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import kr.hhplus.be.server.infrastructure.external.DataPlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;
    private final OrderStatisticsService orderStatisticsService;
    private final DataPlatform dataPlatform;


//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @KafkaListener(topics = "PAYMENT", groupId = "payment-complete-group")
    public void handleOrderEvent(PaymentEvent.Completed event) {
        Order order = orderService.findByOrderId(new OrderCommand.Find(event.getOrderId()));

        var command = OrderStatisticsCommand.AggregatePaidProduct.of(order.getOrderItems());
        orderStatisticsService.paidProductAggregateWithRedis(command);
        dataPlatform.sendPaidData(order);
    }
}
