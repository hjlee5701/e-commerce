package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/coupons")
public class CouponController implements CouponApi {

    private final FakeStore fakeStore;
    private final CouponService couponService;

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
    public ResponseEntity<ApiResult<List<CouponResponse.Issued>>> findHoldingCoupon(
            @PathVariable("id") Long memberId
    ) {
        List<CouponInfo.Issued> responses = couponService.findHoldingCoupons(new CouponCommand.Holdings(memberId));

        var data = Optional.ofNullable(responses)
                .orElse(Collections.emptyList())
                .stream()
                .map(CouponResponse.Issued::from)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_HOLDING_COUPON, data));
    }
}
