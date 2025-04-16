package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Schema(title = "상품 응답")
public class ProductResponse {

    @Getter
    @AllArgsConstructor
    @Schema(title = "상품 조회 응답")
    public static class Detail {
        @Schema(title = "상품 아이디", example = "1")
        private Long productId;

        @Schema(title = "상품명", example = "상의")
        private String title;

        @Schema(title = "상품 가격", example = "10000")
        private BigDecimal price;

        @Schema(title = "잔여 수량", example = "300")
        private Integer quantity;

        public static Detail of(ProductInfo.Detail info) {
            return new Detail(info.getProductId(), info.getTitle(), info.getPrice(), info.getQuantity());
        }
    }

}