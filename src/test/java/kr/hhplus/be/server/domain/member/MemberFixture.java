package kr.hhplus.be.server.domain.member;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MemberFixture implements TestFixture<Member> {

    private final Long id = 1L;
    private final String userId = "tester";

    @Override
    public Member create() {
        Member entity = new Member();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}
