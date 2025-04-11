package kr.hhplus.be.server.domain.memberPoint.exception;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;

public class MemberPointNotFoundException extends ECommerceException {
    public MemberPointNotFoundException() {
        super(MemberPointErrorCode.MEMBER_POINT_NOT_FOUND);
    }
}
