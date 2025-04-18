package kr.hhplus.be.server.interfaces.orderStatistics;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Schema(title = "주문 통계 응답")
public class OrderStatisticsResponse {
    @Getter
    @AllArgsConstructor
    @Schema(title = "(상위 5순위) 인기 상품 응답")
    public static class Popular {
        @Schema(description = "인기 상품 순위", example = "1")
        private int rank;

        @Schema(title = "상품 아이디", example = "1")
        private Long productId;

        @Schema(title = "상품명", example = "상의")
        private String title;

        @Schema(title = "상품 가격", example = "10000")
        private BigDecimal price;

        @Schema(title = "총 상품 수량", example = "300")
        private Long totalSoldQuantity;


        public static Popular of(OrderStatisticsInfo.Popular info) {
            return new OrderStatisticsResponse.Popular(
                    info.getRank(),
                    info.getProductId(),
                    info.getTitle(),
                    info.getPrice(),
                    info.getTotalSoldQuantity()
            );
        }

    }

}
