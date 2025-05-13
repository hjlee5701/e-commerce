package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderStatisticsFacade {

    private final OrderService orderService;
    private final OrderStatisticsService orderStatisticsService;


    public void aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate criteria) {
        List<OrderInfo.Paid> info = orderService.getPaidOrderByDate(criteria.toPaidOrderCommand());
        orderStatisticsService.aggregate(criteria.toAggregateCommand(info));
    }
}
