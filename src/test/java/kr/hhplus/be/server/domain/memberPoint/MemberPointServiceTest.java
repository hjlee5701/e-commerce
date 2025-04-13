package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.exception.InvalidAmountException;
import kr.hhplus.be.server.domain.memberPoint.exception.InvalidBalanceException;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER;
import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy.MAX_CHARGE_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberPointServiceTest {

    @Mock
    private MemberPointRepository memberPointRepository;

    @InjectMocks
    private MemberPointService memberPointService;


    @DisplayName("충전 요청 시, 충전 가능 금액 초과로 예외 발생한다.")
    @Test
    void 충전_가능_금액_초과로_예외() {
        // given
        var command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, ANY_MEMBER, MAX_CHARGE_AMOUNT
        );
        // when, then
        assertThatThrownBy(() -> memberPointService.charge(command))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage(MemberPointErrorCode.INVALID_AMOUNT.getMessage());
    }

    @DisplayName("최초 충전 시, 초기 값인 0원에서 충전된다.")
    @Test
    void 최초_충전은_0원에서_충전() {
        // given
        var amount = BigDecimal.valueOf(100);
        var command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, ANY_MEMBER, amount
        );
        BDDMockito.given(memberPointRepository.findByMemberId(ANY_MEMBER_ID))
                .willReturn(Optional.empty());

        // when
        var result = memberPointService.charge(command);

        // then
        assertThat(result.getBalance()).isEqualTo(BigDecimal.ZERO.add(amount));
    }

    @DisplayName("충전 후, 최대 보유 금액 초과로 에러 발생한다.")
    @Test
    void 최대_보유_금액_초과로_에러() {
        // given
        var command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, ANY_MEMBER, BigDecimal.valueOf(100)
        );

        BDDMockito.given(memberPointRepository.findByMemberId(ANY_MEMBER_ID))
                .willReturn(Optional.of(new MemberPointFixture().maxBalanceMemberPoint()));

        // when, then
        assertThatThrownBy(() -> memberPointService.charge(command))
                .isInstanceOf(InvalidBalanceException.class)
                .hasMessage(MemberPointErrorCode.INVALID_BALANCE.getMessage());
    }

    @DisplayName("유효한 충전 금액, 잔액으로 금액 정보가 성공적으로 저장된다.")
    @Test
    void 충전정보_저장_성공() {
        // given
        var amount = BigDecimal.valueOf(100);
        var command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, ANY_MEMBER, amount
        );
        var memberPoint = new MemberPointFixture().create();
        var expectBalance = memberPoint.getBalance().add(amount);

        BDDMockito.given(memberPointRepository.findByMemberId(ANY_MEMBER_ID))
                .willReturn(Optional.of(memberPoint));

        BDDMockito.given(memberPointRepository.save(memberPoint))
                .willReturn(memberPoint);

        // when
        var result = memberPointService.charge(command);

        // then
        assertThat(result.getBalance()).isEqualTo(expectBalance);
        verify(memberPointRepository, times(1)).save(memberPoint);
    }



}
