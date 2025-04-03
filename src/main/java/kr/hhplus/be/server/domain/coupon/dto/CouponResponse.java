package kr.hhplus.be.server.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(title = "쿠폰 정보 응답")
public class CouponResponse {
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

    @Schema(title = "쿠폰 상태", example = "ISSUED", description = "(ISSUED, USED)")
    private String couponStatus;
}
