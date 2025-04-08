package kr.hhplus.be.server.interfaces.common;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public abstract class ECommerceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public ECommerceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }
}
