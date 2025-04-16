package kr.hhplus.be.server.interfaces.orderStatistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        private Integer totalSoldQuantity;

        @Schema(title = "통계 일자", example = "2025-04-31T14:30:00")
        private LocalDateTime statisticAt;
    }

}
