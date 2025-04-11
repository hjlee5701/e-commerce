package kr.hhplus.be.server.domain.memberPoint.exception;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;

public class InvalidAmountException extends ECommerceException {

    public InvalidAmountException() {
        super(MemberPointErrorCode.INVALID_AMOUNT);
    }
}