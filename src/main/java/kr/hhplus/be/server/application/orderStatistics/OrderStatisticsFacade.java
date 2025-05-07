package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderStatisticsFacade {

    private final OrderService orderService;
    private final OrderStatisticsService orderStatisticsService;


    public void aggregateOrderStatistics(LocalDateTime statisticsAt) {
        List<OrderInfo.Paid> info = orderService.getPaidOrderByDate(statisticsAt);
        orderStatisticsService.aggregate(OrderStatisticsCommand.Aggregate.of(statisticsAt, info));
    }
}
