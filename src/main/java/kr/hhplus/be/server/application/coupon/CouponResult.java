package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
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
        private String couponItemStatus;

        public static Issued of(CouponInfo.Issued info) {
            return new Issued(
                    info.getCouponId(), info.getCouponItemId(), info.getTitle(),
                    info.getIssuedAt(), info.getExpiredAt(), info.getCouponItemStatus()
            );
        }
    }
}
