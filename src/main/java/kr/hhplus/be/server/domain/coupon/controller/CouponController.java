package kr.hhplus.be.server.domain.coupon.controller;

import kr.hhplus.be.server.domain.coupon.dto.CouponResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import kr.hhplus.be.server.global.response.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon")
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

}
