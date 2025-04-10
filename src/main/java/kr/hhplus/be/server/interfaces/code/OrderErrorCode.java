package kr.hhplus.be.server.interfaces.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("OD_001", "주문을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
