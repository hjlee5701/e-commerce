package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER;
import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy.MAX_CHARGE_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberPointTest {

    @Test
    @DisplayName("충전 후 보유 가능한 잔액의 초과로 예외가 발생한다.")
    void 충전_후_보유_잔액_초과() {

        // given
        BigDecimal amount = MAX_CHARGE_AMOUNT.add(BigDecimal.valueOf(100));
        var command = new MemberPointCommand.Charge(ANY_MEMBER_ID, ANY_MEMBER, amount);

        // when & then
        MemberPoint memberPoint = new MemberPointFixture().create();

        assertThatThrownBy(() -> memberPoint.charge(command))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(MemberPointErrorCode.INVALID_BALANCE.getMessage());
    }

    @Test
    @DisplayName("충전 후 잔액은 최대 가능한 금액의 미만 이므로 성공 처리된다.")
    void 유효_잔액으로_충전_성공() {

        // given
        BigDecimal amount = BigDecimal.valueOf(1);
        var command = new MemberPointCommand.Charge(ANY_MEMBER_ID, ANY_MEMBER, amount);

        // when
        MemberPoint memberPoint = new MemberPointFixture().create();

        var afterCharge = memberPoint.getBalance().add(amount);
        memberPoint.charge(command);

        // then
        assertThat(memberPoint.getBalance()).isEqualTo(afterCharge);
    }
}
