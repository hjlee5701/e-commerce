package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;

public class OrderNotFoundException extends ECommerceException {
    public OrderNotFoundException() {
        super(OrderErrorCode.ORDER_NOT_FOUND);
    }
}
