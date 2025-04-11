package kr.hhplus.be.server.domain.member.exception;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.MemberErrorCode;

public class MemberNotFoundException extends ECommerceException {
    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
