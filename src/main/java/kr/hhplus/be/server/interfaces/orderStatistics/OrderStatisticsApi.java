package kr.hhplus.be.server.interfaces.orderStatistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.shared.dto.ApiResult;
import org.springframework.http.ResponseEntity;

import java.util.List;
@Tag(name = "주문 통계", description = "주문 상품 판매량 통계를 관리합니다.")
public interface OrderStatisticsApi {

    @Operation(summary = "인기 상품 조회", description = "상위 5개의 인기 상품을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 상품 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    ResponseEntity<ApiResult<List<OrderStatisticsResponse.Popular>>> findPopularProducts();
}
