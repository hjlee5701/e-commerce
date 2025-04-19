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
        private String couponItemStatus;

        public static CouponInfo.Issued of(Coupon coupon, CouponItem couponItem) {
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

    @Getter
    @AllArgsConstructor
    public static class ItemDetail {
        private Long couponItemId;
        private String title;
        private LocalDateTime issuedAt;
        private LocalDateTime expiredAt;
        private String couponItemStatus;
        public static ItemDetail of(CouponItem item) {
            return new ItemDetail(
                    item.getId(),
                    item.getCoupon().getTitle(),
                    item.getCoupon().getIssuedAt(),
                    item.getCoupon().getExpiredAt(),
                    item.getStatus().name()
            );
        }
    }
}
