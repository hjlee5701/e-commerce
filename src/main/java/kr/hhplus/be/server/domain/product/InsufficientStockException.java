package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;

public class InsufficientStockException extends ECommerceException {

    public InsufficientStockException(String title) {
        super(ProductErrorCode.INSUFFICIENT_STOCK, title);

    }
}
