package kr.hhplus.be.server.domain.memberPoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER;
import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class MemberPointFactoryTest {

    @DisplayName("새롭게 금액 생성시 0원으로 초기화된다.")
    @Test
    void 초기_금액_생성시_0원() {
        // given & when
        MemberPoint point = MemberPointFactory.createInitialPoint(ANY_MEMBER);

        // then
        assertThat(point.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(point.getMember()).isEqualTo(ANY_MEMBER);
    }

    @DisplayName("충전 이력 생성 시 충전 금액과 type 이 저장된다.")
    @Test
    void 충전_이력_생성시_충전_금액과_타입_설정() {
        // given
        var chargeAmount = BigDecimal.valueOf(5000);
        var command = new MemberPointCommand.Charge(ANY_MEMBER_ID, ANY_MEMBER, chargeAmount);

        // when
        MemberPointHistory history = MemberPointFactory.createChargeHistory(command);

        // then
        assertThat(history.getType()).isEqualTo(TransactionType.CHARGE);
        assertThat(history.getAmount()).isEqualByComparingTo("5000");
    }
}
