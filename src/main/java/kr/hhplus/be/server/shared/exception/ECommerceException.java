package kr.hhplus.be.server.shared.exception;


import kr.hhplus.be.server.shared.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ECommerceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public ECommerceException(ErrorCode errorCode, Object... messageArgs) {
        super(String.format(errorCode.getMessage(), messageArgs));
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }
}
