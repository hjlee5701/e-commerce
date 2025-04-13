package kr.hhplus.be.server.interfaces.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("OD_001", "주문을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    NOT_PENDING_ORDER("OD_002","주문 상태가 대기가 아닙니다. 현재 상태: '%s" , HttpStatus.CONFLICT),

    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
