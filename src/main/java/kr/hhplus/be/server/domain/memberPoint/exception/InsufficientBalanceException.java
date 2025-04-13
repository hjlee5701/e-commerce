package kr.hhplus.be.server.domain.memberPoint.exception;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;

public class InsufficientBalanceException extends ECommerceException {
    public InsufficientBalanceException() {
        super(MemberPointErrorCode.INSUFFICIENT_BALANCE);
    }
}
