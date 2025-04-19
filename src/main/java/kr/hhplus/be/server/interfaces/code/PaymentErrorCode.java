package kr.hhplus.be.server.interfaces.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    NOT_PENDING_PAYMENT("P_001","결제 상태가 대기가 아닙니다. 현재 상태: '%s" , HttpStatus.CONFLICT),
    UNMATCHED_ORDER_MEMBER("P_002","결제 권한이 없습니다.", HttpStatus.FORBIDDEN);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
