package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;

public class InvalidDecreaseQuantityException extends ECommerceException {
    public InvalidDecreaseQuantityException() {
        super(ProductErrorCode.INVALID_DECREASE_QUANTITY);

    }
}
