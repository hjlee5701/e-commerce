package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
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

    public List<CouponInfo.Issued> findHoldingCoupons(CouponCommand.Holdings command) {

        List<CouponItem> coupons = couponItemRepository.findAllByMemberId(command.getMemberId()); // fetch join

        return Optional.ofNullable(coupons)
                .orElse(Collections.emptyList())
                .stream()
                .map(CouponInfo.Issued::of)
                .collect(Collectors.toList());
    }

    public CouponItem findByCouponItemId(CouponCommand.Find command) {
        return couponItemRepository.findById(command.getCouponItemId())
                .orElseThrow(() -> new ECommerceException(CouponErrorCode.COUPON_ITEM_NOT_FOUND));
    }

    public CouponInfo.Issuable issuable(CouponCommand.Issuable command) {
        Coupon coupon = couponRepository.findById(command.getCouponItemId())
                .orElseThrow(() -> new ECommerceException(CouponErrorCode.COUPON_NOT_FOUND));

        coupon.issue(LocalDateTime.now());
        return CouponInfo.Issuable.of(coupon);
    }

    public CouponInfo.Issued issue(CouponCommand.Issue command) {
        CouponItem couponItem = CouponFactory.issueCouponItem(command.getMemberId(), command.getCouponId());
        CouponItem issuedItem = couponItemRepository.save(couponItem);
        return CouponInfo.Issued.of(issuedItem);

    }
}
