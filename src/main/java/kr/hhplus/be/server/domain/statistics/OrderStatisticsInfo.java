package kr.hhplus.be.server.domain.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class OrderStatisticsInfo {

    @Getter
    @AllArgsConstructor
    public static class Popular {
        private int rank;
        private Long productId;
        private String title;
        private BigDecimal price;
        private Long totalSoldQuantity;

        public static Popular of(int rank, PopularProductsProjection statistics) {
            return new Popular(
                    rank,
                    statistics.productId(),
                    statistics.title(),
                    statistics.price(),
                    statistics.totalSoldQuantity()
            );
        }
    }
}
