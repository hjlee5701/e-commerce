package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.member.Member;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class MemberPointFactory {

    public static MemberPoint createInitialPoint(Long memberId) {
        return new MemberPoint(1L, Member.referenceById(memberId), BigDecimal.ZERO);
    }

    public static MemberPointHistory createChargeHistory(MemberPointCommand.Charge command) {
        return new MemberPointHistory(1L, Member.referenceById(command.getMemberId()), TransactionType.CHARGE, command.getAmount());
    }
}
