package kr.hhplus.be.server.shared.exception;

import kr.hhplus.be.server.shared.dto.ApiResult;
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
