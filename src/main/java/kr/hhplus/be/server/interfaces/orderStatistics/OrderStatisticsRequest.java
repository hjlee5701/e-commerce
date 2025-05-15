package kr.hhplus.be.server.interfaces.orderStatistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(title = "주문 통계 요청")
public class OrderStatisticsRequest {

    @AllArgsConstructor
    @Getter
    @Schema(title = "결제 상품 저장 요청")
    public static class AddPaidProducts {
        @Schema(title = "상품 아이디", example = "1")
        private Long productId;

        @Schema(title = "판매 수량", example = "3")
        private int totalSoldQuantity;
    }
}