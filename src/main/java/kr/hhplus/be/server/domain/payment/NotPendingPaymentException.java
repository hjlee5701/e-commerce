package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.PaymentErrorCode;

public class NotPendingPaymentException extends ECommerceException {

    public NotPendingPaymentException(String paymentStatus) {
        super(PaymentErrorCode.NOT_PENDING_PAYMENT, paymentStatus);
    }
}