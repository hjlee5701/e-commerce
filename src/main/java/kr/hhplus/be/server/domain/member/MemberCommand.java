package kr.hhplus.be.server.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MemberCommand {


    @Getter
    @AllArgsConstructor
    public static class Find {
        private Long memberId;
    }
}
