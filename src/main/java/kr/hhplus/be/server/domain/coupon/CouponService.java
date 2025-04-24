package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.shared.code.CouponErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponItemRepository couponItemRepository;
    private final CouponRepository couponRepository;

    public List<CouponInfo.ItemDetail> findHoldingCoupons(CouponCommand.Holdings command) {

        List<CouponItem> coupons = couponItemRepository.findAllByMemberId(command.getMemberId()); // fetch join

        return Optional.ofNullable(coupons)
                .orElse(Collections.emptyList())
                .stream()
                .map(CouponInfo.ItemDetail::of)
                .collect(Collectors.toList());
    }

    public CouponItem findByCouponItemId(CouponCommand.Find command) {
        return couponItemRepository.findByIdForUpdate(command.getCouponItemId())
                .orElseThrow(() -> new ECommerceException(CouponErrorCode.COUPON_ITEM_NOT_FOUND));
    }

    public CouponInfo.Issued issue(CouponCommand.Issue command) {
        Coupon coupon = couponRepository.findByIdForUpdate(command.getCouponId())
                .orElseThrow(() -> new ECommerceException(CouponErrorCode.COUPON_NOT_FOUND));

        // 쿠폰 발급
        CouponItem couponItem = coupon.issue(LocalDateTime.now(), command.getMemberId());

        // 발급 쿠폰 저장
        CouponItem savedCouponItem = couponItemRepository.save(couponItem);

        return CouponInfo.Issued.of(coupon, savedCouponItem);
    }
}
