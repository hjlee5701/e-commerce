package kr.hhplus.be.server.shared.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    COUPON_ITEM_NOT_FOUND("C_001", "쿠폰 아이템 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNUSABLE_COUPON_ITEM("C_002", "사용할 수 없는 쿠폰입니다.", HttpStatus.CONFLICT),
    COUPON_NOT_YET_ACTIVE("C_003", "쿠폰은 '%s' 이후부터 사용 가능합니다.", HttpStatus.CONFLICT),
    COUPON_EXPIRED("C_004", "쿠폰 유효기간 ('%s') 이 지났습니다.", HttpStatus.CONFLICT),
    COUPON_ITEM_ACCESS_DENIED("C_005", "해당 쿠폰은 현재 회원의 소유가 아닙니다.", HttpStatus.FORBIDDEN),
    COUPON_NOT_FOUND("C_006", "쿠폰이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    COUPON_INACTIVE("C_007", "쿠폰 상태가 비활성화되어 있습니다.", HttpStatus.CONFLICT),
    COUPON_NO_REMAINING("C_008", "쿠폰의 잔여 수량이 부족합니다.", HttpStatus.CONFLICT)
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
