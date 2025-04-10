package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class MemberPointCommand {

    @EqualsAndHashCode
    @Getter
    @AllArgsConstructor
    public static class Charge {
        private Long memberId;
        private Member member;
        private BigDecimal amount;
    }

    @Getter
    @AllArgsConstructor
    public static class Use {
        private BigDecimal amount;
        private Long memberId;

    }
}
