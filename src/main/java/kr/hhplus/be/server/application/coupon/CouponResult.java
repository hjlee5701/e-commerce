package kr.hhplus.be.server.application.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class CouponResult {

    @AllArgsConstructor
    @Getter
    public static class Issued {
        private Long couponId;
        private Long couponItemId;
        private String title;
        private LocalDateTime issuedAt;
        private LocalDateTime expiredAt;
        private String couponStatus;

        public static Issued of(Coupon coupon, CouponItem couponItem) {
            return new Issued(
                    coupon.getId(), couponItem.getId(), coupon.getTitle(),
                    coupon.getIssuedAt(), coupon.getExpiredAt(), couponItem.getStatus().name()
            );
        }
    }
}
