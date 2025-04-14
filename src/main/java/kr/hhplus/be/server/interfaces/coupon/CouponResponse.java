package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Schema(title = "쿠폰 정보 응답")
public class CouponResponse {

    @Getter
    @AllArgsConstructor
    @Schema(title = "발급된 쿠폰 응답")
    public static class Issued {
        @Schema(title = "쿠폰 아이디", example = "1", description = "쿠폰 아이디입니다.")
        private Long couponId;

        @Schema(title = "쿠폰 아이템 아이디", example = "50", description = "발급 받은 쿠폰의 아이디입니다.")
        private Long couponItemId;

        @Schema(title = "쿠폰명", example = "선착순 할인 쿠폰")
        private String title;

        @Schema(title = "발급 일자", example = "2025-04-04T14:30:00")
        private LocalDateTime issuedAt;

        @Schema(title = "쿠폰 만료일", example = "2025-04-31T14:30:00")
        private LocalDateTime expiredAt;

        @Schema(title = "쿠폰 아이템 상태", example = "ISSUED", description = "(ISSUED, USED)")
        private String couponItemStatus;

        public static Issued of(CouponResult.Issued result) {
            return new Issued(
                    result.getCouponId(),
                    result.getCouponItemId(),
                    result.getTitle(),
                    result.getIssuedAt(),
                    result.getExpiredAt(),
                    result.getCouponItemStatus()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(title = "쿠폰 조회 응답")
    public static class ItemDetail {
        @Schema(title = "쿠폰 아이템 아이디", example = "50", description = "발급 받은 쿠폰의 아이디입니다.")
        private Long couponItemId;

        @Schema(title = "쿠폰명", example = "선착순 할인 쿠폰")
        private String title;

        @Schema(title = "발급 일자", example = "2025-04-04T14:30:00")
        private LocalDateTime issuedAt;

        @Schema(title = "쿠폰 만료일", example = "2025-04-31T14:30:00")
        private LocalDateTime expiredAt;

        @Schema(title = "쿠폰 아이템 상태", example = "ISSUED", description = "(ISSUED, USED)")
        private String couponItemStatus;

        public static ItemDetail of(CouponInfo.ItemDetail result) {
            return new ItemDetail(
                    result.getCouponItemId(),
                    result.getTitle(),
                    result.getIssuedAt(),
                    result.getExpiredAt(),
                    result.getCouponItemStatus()
            );
        }
    }

}
