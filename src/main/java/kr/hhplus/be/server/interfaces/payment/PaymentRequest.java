package kr.hhplus.be.server.interfaces.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Tag(name = "결제 요청")
@NoArgsConstructor
public class PaymentRequest {

    @Getter
    @AllArgsConstructor
    @Schema(title = "결제 완료 요청")
    public static class Pay{
        @Schema(title = "쿠폰 아이디", example = "50", description = "사용자 보유 쿠폰 (필수값 아니다.)")
        private Long couponItemId;

        @Schema(title = "사용자 아이디", example = "1", description = "결제 요청한 사용자 아이디")
        private Long memberId;

    }
}
