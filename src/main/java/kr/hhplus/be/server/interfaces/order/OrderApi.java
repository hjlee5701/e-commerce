package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import org.springframework.http.ResponseEntity;

@Tag(name = "주문 관리", description = "주문을 관리합니다.")
public interface OrderApi {

    @Operation(summary = "상품 주문", description = "사용자가 상품을 주문합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content()),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자", content = @Content()),
            @ApiResponse(responseCode = "400", description = "유효하지 않는 쿠폰 / 상품 재고 부족 / 주문 실패", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    ResponseEntity<ApiResult<OrderResponse.Created>> order(
            @Parameter(description = "사용자 아이디") Long memberId,
            @Parameter(description = "주문 요청") OrderRequest.Create orderRequest
    );

}
