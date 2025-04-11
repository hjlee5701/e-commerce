package kr.hhplus.be.server.interfaces.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import org.springframework.http.ResponseEntity;

@Tag(name = "결제 관리", description = "결제를 관리합니다.")
public interface PaymentApi {

    @Operation(summary = "주문 결제", description = "주문 처리 완료 후 결제를 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 결제 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자", content = @Content()),
            @ApiResponse(responseCode = "400", description = "유효하지 않는 쿠폰 / 보유 금액 초과 주문 / 결제 실패", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    public ResponseEntity<ApiResult<PaymentResponse.Paid>> orderPayment(
            @Parameter(description = "주문 아이디") Long orderId,
            @Parameter(description = "결제 요청") PaymentRequest.Pay request
    );


}
