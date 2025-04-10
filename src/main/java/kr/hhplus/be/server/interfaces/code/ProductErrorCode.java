package kr.hhplus.be.server.interfaces.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND("P_001", "상품 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK("P_002", "상품 '%s' 의 재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
