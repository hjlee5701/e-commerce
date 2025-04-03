package kr.hhplus.be.server.domain.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.dto.CouponResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import kr.hhplus.be.server.global.response.SuccessCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "쿠폰 관리", description = "쿠폰을 관리합니다.")
public interface CouponApi {

    @Operation(summary = "쿠폰 발급", description = "선착순으로 쿠폰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 사용자", content = @Content()),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 쿠폰", content = @Content()),
            @ApiResponse(responseCode = "409", description = "쿠폰 수량 없음", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    ResponseEntity<ApiResult<CouponResponse>> issue(
            @Parameter(description = "쿠폰 아이디") Long couponId,
            @Parameter(description = "사용자 아이디") Long memberId
    );
}
