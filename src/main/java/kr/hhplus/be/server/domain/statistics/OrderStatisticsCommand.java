package kr.hhplus.be.server.domain.statistics;

import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class OrderStatisticsCommand {

    @Getter
    @AllArgsConstructor
    public static class Aggregate {

        private LocalDateTime statisticsAt;
        private List<AggregateItem> aggregateItems;

        public static Aggregate of(LocalDateTime now, List<OrderInfo.Paid> info) {
            return new Aggregate(now, AggregateItem.of(info));
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
}
