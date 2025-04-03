package kr.hhplus.be.server.domain.memberPoint.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.memberPoint.dto.ChargePointRequest;
import kr.hhplus.be.server.domain.memberPoint.dto.ChargePointResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자 금액 관리", description = "사용자 금액 충전 및 조회")
public interface MemberPointApi {

    @Operation(summary = "사용자 금액 충전", description = "금액을 충전합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "금액 충전 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 사용자", content = @Content()),
            @ApiResponse(responseCode = "400", description = "충전 정책 위반", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    ResponseEntity<ApiResult<ChargePointResponse>> charge(
            @Parameter(description = "사용자 아이디") Long memberId,
            @Parameter(description = "충전 요청 정보") ChargePointRequest chargePointRequest
    );
}

