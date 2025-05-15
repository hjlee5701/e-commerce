package kr.hhplus.be.server.domain.statistics;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class OrderStatisticsCommand {

    @Getter
    @AllArgsConstructor
    public static class Aggregate {

        private LocalDate startDate;
        private LocalDate endDate;
        private List<AggregateItem> aggregateItems;

        public static Aggregate of(LocalDate startDate, LocalDate endDate, List<OrderInfo.Paid> info) {
            return new Aggregate(startDate, endDate, AggregateItem.of(info));
        }

        public Map<Long, Integer> toSoldProductMap() {
            return aggregateItems.stream()
                    .collect(Collectors.toMap(
                            AggregateItem::getProductId,
                            AggregateItem::getSoldQuantity,
                            Integer::sum  // 중복 키 시 수량 누적
                    ));
        }

    }

    @Getter
    @AllArgsConstructor
    public static class AggregateItem {

        private Long productId;
        private Integer soldQuantity;

        public static List<AggregateItem> of(List<OrderInfo.Paid> info) {
            return info.stream().map(
                    paidInfo -> new AggregateItem(
                            paidInfo.getProductId(),
                            paidInfo.getOrderQuantity())
            ).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Popular {
        private LocalDate startDate;
        private LocalDate endDate;
        private int count;
        public static Popular of(LocalDate startDate, LocalDate endDate, int count) {
            return new Popular(startDate, endDate, count);
        }
    }


    @Getter
    @AllArgsConstructor
    public static class AggregatePaidProduct {

        private LocalDateTime startDateTime;
        private Map<Long, Integer> productMap;

        public static AggregatePaidProduct of(List<OrderItem> orderItems) {
            Map<Long, Integer> productMap = orderItems.stream()
                    .collect(Collectors.toMap(orderItem ->
                                    orderItem.getProduct().getId(),
                            OrderItem::getQuantity,
                            Integer::sum // 상품 ID 중복 처리: 값을 합침
                    ));
            return new AggregatePaidProduct(LocalDateTime.now(), productMap);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PopularWithRedis {
        private LocalDate yesterday;
        private int days;
        private int topN;
        public static PopularWithRedis of(LocalDate yesterday, int days, int count) {
            return new PopularWithRedis(yesterday, days, count);
        }
    }

}
