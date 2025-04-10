package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Schema(title = "상품 주문 요청")
public class OrderRequest {

    @Getter
    @AllArgsConstructor
    @Schema(title = "상품 주문 생성 요청")
    public static class Create {
        @Schema(title = "쿠폰 아이디", example = "50", description = "사용자 보유 쿠폰 (필수값 아니다.)")
        private Long couponItemId;

        @Schema(title = "상품 목록", description = "주문 요청한 상품 리스트")
        private List<ItemCreate> orderItems;
    }

    @Getter
    @AllArgsConstructor
    @Schema(title = "주문 요청한 상품 정보")
    public static class ItemCreate {
        @Schema(title = "상품 아이디", example = "1")
        private Long productId;

        @Schema(title = "주문 수량", example = "1")
        private Integer quantity;

    }
}
