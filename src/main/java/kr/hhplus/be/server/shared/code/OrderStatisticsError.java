package kr.hhplus.be.server.shared.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderStatisticsError implements ErrorCode{
    INVALID_SOLD_QUANTITY("OS_001", "판매 수량은 1개 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
