package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.member.Member;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class MemberPointFactory {

    public static MemberPoint createInitialPoint(Member member) {
        return new MemberPoint(1L, member, BigDecimal.ZERO);
    }

    public static MemberPointHistory createChargeHistory(MemberPointCommand.Charge command) {
        return new MemberPointHistory(1L, command.getMember(), TransactionType.CHARGE, command.getAmount());
    }
}
