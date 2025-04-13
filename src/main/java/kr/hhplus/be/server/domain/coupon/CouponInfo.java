package kr.hhplus.be.server.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class CouponInfo {

    @Getter
    @AllArgsConstructor
    public static class Issued {
        private Long couponId;
        private Long couponItemId;
        private String title;
        private LocalDateTime issuedAt;
        private LocalDateTime expiredAt;
        private String couponStatus;

        public static CouponInfo.Issued from(CouponItem couponItem) {
            Coupon coupon = couponItem.getCoupon();
            return new CouponInfo.Issued(
                    coupon.getId(),
                    couponItem.getId(),
                    coupon.getTitle(),
                    coupon.getIssuedAt(),
                    coupon.getExpiredAt(),
                    couponItem.getStatus().name()
            );

        }
    }
}
