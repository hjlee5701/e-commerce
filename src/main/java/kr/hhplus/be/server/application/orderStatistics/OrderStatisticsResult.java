package kr.hhplus.be.server.application.orderStatistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderStatisticsResult {
    @Getter
    @AllArgsConstructor
    public static class PopularWithRedis implements Serializable {
        private static final long serialVersionUID = 1L;

        private int rank;
        private Long productId;
        private String title;
        private BigDecimal price;
    }
}
