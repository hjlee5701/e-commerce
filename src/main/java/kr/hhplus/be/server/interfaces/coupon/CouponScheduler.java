package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponService couponService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void issueCoupon() {
        List<Coupon> coupons = couponService.getAllAvailable();
        int count = 20;
        for (Coupon coupon : coupons) {
            couponService.processCoupon(coupon, count);
        }
    }
}
