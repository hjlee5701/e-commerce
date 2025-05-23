package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderStatisticsFacade {

    private final OrderService orderService;
    private final OrderStatisticsService orderStatisticsService;
    private final ProductService productService;

    private static final int TOP_N = 5;
    private static final int DAYS = 3;



    public void aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate criteria) {
        List<OrderInfo.Paid> info = orderService.getPaidOrderByDate(criteria.toPaidOrderCommand());
        orderStatisticsService.aggregate(criteria.toAggregateCommand(info));
    }

    public void dailyOrderStatisticsWithRedis(OrderStatisticsCriteria.Aggregate criteria) {
        List<OrderInfo.Paid> info = orderService.getPaidOrderByDate(criteria.toPaidOrderCommand());
        orderStatisticsService.dailyOrderStatistics(criteria.toDailyCommand(info));
    }

    public List<OrderStatisticsResult.PopularWithRedis> popularWithRedis() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        Map<Long, Double> topProducts = orderStatisticsService.popularWithRedis(OrderStatisticsCommand.PopularWithRedis.of(yesterday, DAYS, TOP_N));

        List<ProductInfo.Detail> info = productService.findProductsByIds(ProductCommand.FindAll.of(topProducts.keySet()));

        Map<Long, ProductInfo.Detail> productDetailMap = info
                .stream()
                .collect(Collectors.toMap(ProductInfo.Detail::getProductId, Function.identity()));


        List<OrderStatisticsResult.PopularWithRedis> popularList = new ArrayList<>();
        AtomicInteger rank = new AtomicInteger(1);

        topProducts.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue(Comparator.reverseOrder()))  // 판매량 내림차순 정렬
                .forEachOrdered(entry -> {
                    Long productId = entry.getKey();

                    ProductInfo.Detail detail = productDetailMap.get(productId);
                    if (detail != null) {
                        popularList.add(new OrderStatisticsResult.PopularWithRedis(
                                rank.getAndIncrement(),
                                productId,
                                detail.getTitle(),
                                detail.getPrice()
                        ));
                    }
                });
        return popularList;
    }
}
