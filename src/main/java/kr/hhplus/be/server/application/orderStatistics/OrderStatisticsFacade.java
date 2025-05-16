package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsInfo;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class OrderStatisticsFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final OrderStatisticsService orderStatisticsService;


    public void aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate criteria) {
        List<OrderInfo.Paid> info = orderService.getPaidOrderByDate(criteria.toPaidOrderCommand());
        orderStatisticsService.aggregate(criteria.toAggregateCommand(info));
    }

    @Transactional
    public List<OrderStatisticsInfo.Popular> popular() {
        // 인기 상품 아이디 조회
        LocalDate now = LocalDate.now();
        OrderStatisticsCommand.PopularProductIds command = OrderStatisticsCommand.PopularProductIds.of(now.minusDays(3), now, 5);
        List<Long> productIds = orderStatisticsService.popularProductIds(command);

        // 상품 정보 조회
        List<ProductInfo.Detail> productDetails = productService.findPopularProductsByIds(productIds);

        return IntStream.range(0, productDetails.size())
                .mapToObj(i -> OrderStatisticsInfo.Popular.of(i + 1, productDetails.get(i)))
                .toList();
    }
}
