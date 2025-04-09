package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;

import static kr.hhplus.be.server.interfaces.code.ProductErrorCode.PRODUCT_NOT_FOUND;

public class ProductNotFoundException extends ECommerceException {
    public ProductNotFoundException() {
        super(PRODUCT_NOT_FOUND);
    }
}