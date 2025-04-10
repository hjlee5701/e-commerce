package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;

public class NotPendingOrderException extends ECommerceException {

    public NotPendingOrderException(String currentStatus) {
        super(OrderErrorCode.NOT_PENDING_ORDER, currentStatus);
    }
}
