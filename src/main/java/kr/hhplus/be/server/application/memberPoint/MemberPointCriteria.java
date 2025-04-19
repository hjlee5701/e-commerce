package kr.hhplus.be.server.application.memberPoint;

import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class MemberPointCriteria {

    @Getter
    @AllArgsConstructor
    public static class Charge {
        private Long memberId;
        private BigDecimal amount;

        public MemberPointCommand.Charge toChargeCommand() {
            return new MemberPointCommand.Charge(memberId, amount);
        }

        public MemberCommand.Find toFindMemberCommand() {
            return new MemberCommand.Find(memberId);
        }
    }
}
