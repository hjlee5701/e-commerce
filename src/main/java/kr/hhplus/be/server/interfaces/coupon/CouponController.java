package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
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

    private final CouponService couponService;
    private final CouponFacade facade;

    @Override
    @PostMapping("{couponId}/member/{id}")
    public ResponseEntity<ApiResult<CouponResponse.Issued>> issue (
            @PathVariable("couponId") Long couponId,
            @PathVariable("id") Long memberId
    ) {

        CouponResult.Issued result = facade.issue(CouponCriteria.Issue.of(couponId, memberId));
        var data = CouponResponse.Issued.of(result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ISSUE_COUPON, data));
    }


    @Override
    @GetMapping("{id}")
    public ResponseEntity<ApiResult<List<CouponResponse.ItemDetail>>> findHoldingCoupon(
            @PathVariable("id") Long memberId
    ) {
        List<CouponInfo.ItemDetail> responses = couponService.findHoldingCoupons(new CouponCommand.Holdings(memberId));

        var data = Optional.ofNullable(responses)
                .orElse(Collections.emptyList())
                .stream()
                .map(CouponResponse.ItemDetail::of)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_HOLDING_COUPON, data));
    }
}
