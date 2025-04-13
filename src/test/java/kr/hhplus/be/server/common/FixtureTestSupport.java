package kr.hhplus.be.server.common;

import kr.hhplus.be.server.domain.member.Member;

import java.time.LocalDateTime;


public abstract class FixtureTestSupport {
    public static final Long ANY_MEMBER_ID = 1L;
    public static final Member ANY_MEMBER = new Member();
    public static final LocalDateTime FIXED_NOW = LocalDateTime.of(2024, 4, 10, 12, 0);

}
