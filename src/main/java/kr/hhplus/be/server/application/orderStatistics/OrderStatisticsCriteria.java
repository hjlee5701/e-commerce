package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
public class OrderStatisticsCriteria {

    @Getter
    @AllArgsConstructor
    public static class Aggregate {
        private LocalDate startDate;
        private LocalDate endDate;

        public static Aggregate of (LocalDate endDate) {
            return new Aggregate(endDate.minusDays(1), endDate);
        }

        public OrderCommand.PaidStatistics toPaidOrderCommand() {
            return new OrderCommand.PaidStatistics(startDate.atStartOfDay(), endDate.atStartOfDay());
        }

        public OrderStatisticsCommand.Aggregate toAggregateCommand(List<OrderInfo.Paid> info) {
            return OrderStatisticsCommand.Aggregate.of(startDate, endDate, info);
        }

        public OrderStatisticsCommand.Aggregate toDailyCommand(List<OrderInfo.Paid> info) {
            return OrderStatisticsCommand.Aggregate.of(null, endDate, info);
        }


    }

}
