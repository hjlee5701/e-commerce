package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
public class OrderStatisticsFacadeTest {

    @InjectMocks
    private OrderStatisticsFacade orderStatisticsFacade;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderStatisticsService orderStatisticsService;


    @Test
    @DisplayName("orderService → orderStatisticsService 순서로 호출되는지 검증")
    void 순서대로_호출되는지_검증() {
        // given
        LocalDateTime now = FIXED_NOW;

        List<OrderInfo.Paid> orders = List.of(
                new OrderInfo.Paid(1L, 10), new OrderInfo.Paid(2L, 20)
        );

        given(orderService.getPaidOrderByDate(now)).willReturn(orders);

        // when
        orderStatisticsFacade.aggregateOrderStatistics(now);

        // then
        InOrder inOrder = inOrder(orderService, orderStatisticsService);
        inOrder.verify(orderService).getPaidOrderByDate(any(LocalDateTime.class));
        inOrder.verify(orderStatisticsService).aggregate(any(OrderStatisticsCommand.Aggregate.class));
    }
}
