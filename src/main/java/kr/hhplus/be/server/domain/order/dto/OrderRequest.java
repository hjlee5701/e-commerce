package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Schema(title = "상품 주문 요청")
public class OrderRequest {

    @Schema(title = "쿠폰 아이디", example = "50", description = "사용자 보유 쿠폰 (필수값 아니다.)")
    private Long couponItemId;

    @Schema(title = "상품 목록", description = "주문 요청한 상품 리스트")
    private List<OrderItemResponse> orderItems;
}
