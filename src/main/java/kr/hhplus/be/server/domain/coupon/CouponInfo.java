package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.member.Member;
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

        public static CouponInfo.Issued of(CouponItem couponItem) {
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

    @Getter
    @AllArgsConstructor
    public static class Issuable {
        private Long couponId;
        private String title;
        private LocalDateTime issuedAt;
        private LocalDateTime expiredAt;

        public static Issuable of(Coupon coupon) {
            return new Issuable(
                    coupon.getId(),
                    coupon.getTitle(),
                    coupon.getIssuedAt(),
                    coupon.getExpiredAt()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ItemDetail {
        private Long couponItemId;
        private Long memberId;
        private Long couponId;
        private CouponItemStatus status;
        public static ItemDetail of(CouponItem item) {
            return new ItemDetail(item.getId(), item.getMember().getId(), item.getCoupon().getId(), item.getStatus());
        }
    }
}
