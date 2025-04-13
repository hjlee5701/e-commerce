package kr.hhplus.be.server.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class MemberInfo {

    @AllArgsConstructor
    @Getter
    public static class Detail {
        private Long memberId;
        private String userId;
        private LocalDateTime regAt;

        public static Detail of(Member member) {
            return new Detail(member.getId(), member.getUserId(), member.getRegAt());
        }
    }

}
