package kr.hhplus.be.server.shared.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LockErrorCode implements ErrorCode {
    ALREADY_LOCKED("LK_001", "이미 락이 점유된 상태입니다. 키: %s", HttpStatus.CONFLICT),
    AOP_ERROR("LK_002", "AOP 예외 처리 중 checked 발생 ", HttpStatus.INTERNAL_SERVER_ERROR),
    UNHOLD_LOCK("LK_003", "현재 스레드는 락을 보유하고 있지 않습니다. 키: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
