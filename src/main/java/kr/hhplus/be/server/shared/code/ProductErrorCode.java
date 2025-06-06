package kr.hhplus.be.server.shared.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND("P_001", "상품 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK("P_002", "상품의 재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    INVALID_DECREASE_QUANTITY("P_003", "차감 수량이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_STOCK_NOT_FOUND("P_004","상품의 재고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
