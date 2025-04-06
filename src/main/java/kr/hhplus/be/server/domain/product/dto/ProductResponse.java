package kr.hhplus.be.server.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Schema(title = "상품 조회 응답")
public class ProductResponse {

    @Schema(title = "상품 아이디", example = "1")
    private Long productId;

    @Schema(title = "상품명", example = "상의")
    private String title;

    @Schema(title = "상품 가격", example = "10000")
    private BigDecimal price;

    @Schema(title = "잔여 수량", example = "300")
    private Integer quantity;
}