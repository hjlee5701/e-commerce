package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class MemberPointFixture implements TestFixture<MemberPoint> {

    private final Long id = 1L;
    private BigDecimal balance = BigDecimal.ZERO;

    @Override
    public MemberPoint create() {
        MemberPoint entity = new MemberPoint();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}
