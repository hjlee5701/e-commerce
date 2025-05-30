package kr.hhplus.be.server.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Issued {
        private Long couponId;
        private String memberId;

        public static Issued of(Long couponId, String memberId) {
            return new Issued(couponId, memberId);
        }
    }
}
