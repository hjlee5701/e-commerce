package kr.hhplus.be.server.domain.member;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MemberCommand {

    @EqualsAndHashCode
    @Getter
    @AllArgsConstructor
    public static class Find {
        private Long memberId;
    }
}
