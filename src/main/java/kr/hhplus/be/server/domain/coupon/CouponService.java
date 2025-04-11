package kr.hhplus.be.server.domain.coupon;

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
                .map(CouponInfo.Issued::from)
                .collect(Collectors.toList());
    }

    public CouponItem findByCouponItemId(CouponCommand.Find command) {
        return couponItemRepository.findById(command.getCouponItemId())
                .orElseThrow(CouponItemNotFoundException::new);

    }

    public Coupon issuable(CouponCommand.Issuable command) {
        Coupon coupon = couponRepository.findById(command.getCouponItemId())
                .orElseThrow(CouponNotFoundException::new);

        coupon.issue(LocalDateTime.now());
        return coupon;
    }

    public CouponItem issue(CouponCommand.Issue command) {
        CouponItem couponItem = CouponFactory.issueCouponItem(command.getMember(), command.getCoupon());
        return couponItemRepository.save(couponItem);
    }
}
