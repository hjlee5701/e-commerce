package kr.hhplus.be.server.domain.memberPoint.exception;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;

public class InvalidBalanceException extends ECommerceException {

    public InvalidBalanceException() {
        super(MemberPointErrorCode.INVALID_BALANCE);
    }
}
