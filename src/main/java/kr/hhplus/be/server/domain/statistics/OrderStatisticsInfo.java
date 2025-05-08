package kr.hhplus.be.server.domain.statistics;

import kr.hhplus.be.server.domain.product.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
public class OrderStatisticsInfo {

    @Getter
    @AllArgsConstructor
    public static class Popular {
        private int rank;
        private Long productId;
        private String title;
        private BigDecimal price;

        public static Popular of(int rank, PopularProductsProjection statistics) {
            return new Popular(
                    rank,
                    statistics.productId(),
                    statistics.title(),
                    statistics.price()
            );
        }
        public static Popular of(int rank, ProductInfo.Detail productDetail) {
            return new Popular(
                    rank,
                    productDetail.getProductId(),
                    productDetail.getTitle(),
                    productDetail.getPrice()
            );
        }
    }
}
