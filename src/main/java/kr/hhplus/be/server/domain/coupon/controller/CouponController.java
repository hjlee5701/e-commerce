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
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/coupons")
public class CouponController implements CouponApi {

    private final FakeStore fakeStore;

    @Override
    @PostMapping("{couponId}/member/{id}")
    public ResponseEntity<ApiResult<CouponResponse>> issue (
            @PathVariable("couponId") Long couponId,
            @PathVariable("id") Long memberId
    ) {
        CouponResponse response = fakeStore.coupon();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ISSUE_COUPON, response));
    }


    @Override
    @GetMapping("{id}")
    public ResponseEntity<ApiResult<List<CouponResponse>>> checkHoldingCoupon(
            @PathVariable("id") Long memberId
    ) {
        List<CouponResponse> responses = fakeStore.couponList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.CHECK_HOLDING_COUPON, responses));
    }
}
