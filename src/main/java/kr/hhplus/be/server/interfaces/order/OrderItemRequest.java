package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(title = "주문 상품 정보 요청")
public class OrderItemRequest {

    @Getter
    @AllArgsConstructor
    @Schema(title = "주문 요청한 상품 정보")
    public static class Item {
        @Schema(title = "상품 아이디", example = "1")
        private Long productId;

        @Schema(title = "주문 수량", example = "1")
        private Integer quantity;

    }
}
