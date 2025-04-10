package kr.hhplus.be.server.interfaces.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    COUPON_ITEM_NOT_FOUND("C_001", "쿠폰 아이템 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNUSABLE_COUPON_ITEM("C_002", "사용할 수 없는 쿠폰입니다.", HttpStatus.CONFLICT),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
