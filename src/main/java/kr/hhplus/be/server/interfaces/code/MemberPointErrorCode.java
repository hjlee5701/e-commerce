package kr.hhplus.be.server.interfaces.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberPointErrorCode implements ErrorCode {
    INVALID_AMOUNT("MP_001", "충전 가능한 금액을 초과했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_BALANCE("MP_002", "보유 가능한 금액을 초과했습니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}