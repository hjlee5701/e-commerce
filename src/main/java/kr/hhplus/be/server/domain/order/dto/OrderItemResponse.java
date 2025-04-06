package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Schema(title = "주문 상품 정보")
public class OrderItemResponse {

    @Schema(title = "상품 아이디", example = "1")
    private Long productId;

    @Schema(title = "상품명", example = "상의")
    private String title;

    @Schema(title = "상품 가격", example = "10000")
    private BigDecimal price;

    @Schema(title = "주문 수량", example = "1")
    private Integer quantity;

}
