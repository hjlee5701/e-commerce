package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.application.memberPoint.MemberPointCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberPointFactory {

    public static MemberPointHistory createChargeHistory(MemberPointCommand.Charge command) {
        return new MemberPointHistory(1L, command.getMember(), TransactionType.CHARGE, command.getAmount());
    }
}
