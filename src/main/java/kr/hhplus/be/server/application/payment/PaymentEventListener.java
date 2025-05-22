package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import kr.hhplus.be.server.infrastructure.external.DataPlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;
    private final OrderStatisticsService orderStatisticsService;
    private final DataPlatform dataPlatform;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(PaymentEvent.Completed paymentEvent) {
        Order order = orderService.findByOrderId(new OrderCommand.Find(paymentEvent.orderId()));

        var command = OrderStatisticsCommand.AggregatePaidProduct.of(order.getOrderItems());
        orderStatisticsService.paidProductAggregateWithRedis(command);
        dataPlatform.sendPaidData(order);
    }
}
