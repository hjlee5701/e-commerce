package kr.hhplus.be.server.interfaces.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("ORD_001", "주문을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    NOT_PENDING_ORDER("ORD_002","주문 상태가 대기가 아닙니다. 현재 상태: '%s" , HttpStatus.CONFLICT),
    INVALID_ORDER_PRODUCT("ORD_003", "유효하지 않은 상품이 포함되어 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_QUANTITY("ORD_004", "주문 수량은 1개 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN_ORDER_ACCESS("ORD_005", "해당 주문에 접근할 수 있는 권한이 없습니다.", HttpStatus.FORBIDDEN)
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
