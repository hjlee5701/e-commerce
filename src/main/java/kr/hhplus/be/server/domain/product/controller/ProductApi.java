package kr.hhplus.be.server.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.product.dto.ProductResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import org.springframework.http.ResponseEntity;

@Tag(name = "상품 관리", description = "상품을 관리합니다.")
public interface ProductApi {

    @Operation(summary = "상품 조회", description = "상품을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    ResponseEntity<ApiResult<ProductResponse>> findProduct(
            @Parameter(description = "상품 아이디") Long productId
    );
}
