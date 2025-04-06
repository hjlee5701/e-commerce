package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
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
    public ResponseEntity<ApiResult<CouponResponse.Issued>> issue (
            @PathVariable("couponId") Long couponId,
            @PathVariable("id") Long memberId
    ) {
        CouponResponse.Issued response = fakeStore.coupon();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ISSUE_COUPON, response));
    }


    @Override
    @GetMapping("{id}")
    public ResponseEntity<ApiResult<List<CouponResponse.Issued>>> checkHoldingCoupon(
            @PathVariable("id") Long memberId
    ) {
        List<CouponResponse.Issued> responses = fakeStore.couponList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.CHECK_HOLDING_COUPON, responses));
    }
}
