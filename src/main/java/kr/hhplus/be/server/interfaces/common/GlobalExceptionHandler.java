package kr.hhplus.be.server.interfaces.common;

import kr.hhplus.be.server.domain.common.ECommerceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ECommerceException.class)
    public ResponseEntity<ApiResult<Void>> handleECommerceException(ECommerceException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResult.of(ex.getMessage(), ex.getErrorCode()));
    }
}
