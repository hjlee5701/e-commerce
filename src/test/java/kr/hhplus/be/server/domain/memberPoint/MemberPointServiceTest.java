package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.shared.code.MemberPointErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy.MAX_CHARGE_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberPointServiceTest {

    @Mock
    private MemberPointHistoryRepository memberPointHistoryRepository;

    @Mock
    private MemberPointRepository memberPointRepository;

    @InjectMocks
    private MemberPointService memberPointService;


    @DisplayName("충전 요청 시, 충전 가능 금액 초과로 예외 발생한다.")
    @Test
    void 충전_가능_금액_초과로_예외() {
        // given
        var command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, MAX_CHARGE_AMOUNT
        );
        // when, then
        assertThatThrownBy(() -> memberPointService.charge(command))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(MemberPointErrorCode.INVALID_AMOUNT.getMessage());
    }


    @DisplayName("충전 후, 최대 보유 금액 초과로 에러 발생한다.")
    @Test
    void 최대_보유_금액_초과로_에러() {
        // given
        var command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, BigDecimal.valueOf(100)
        );

        given(memberPointRepository.findByMemberId(ANY_MEMBER_ID))
                .willReturn(Optional.of(new MemberPointFixture().maxBalanceMemberPoint()));

        // when, then
        assertThatThrownBy(() -> memberPointService.charge(command))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(MemberPointErrorCode.INVALID_BALANCE.getMessage());
    }

    @DisplayName("유효한 충전 금액, 잔액으로 금액 정보와 이력이 성공적으로 저장된다.")
    @Test
    void 충전정보_저장_성공() {
        // given
        var amount = BigDecimal.valueOf(100);
        var command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, amount
        );
        var memberPoint = new MemberPointFixture().create();
        var expectBalance = memberPoint.getBalance().add(amount);

        given(memberPointRepository.findByMemberId(ANY_MEMBER_ID))
                .willReturn(Optional.of(memberPoint));

        given(memberPointRepository.save(memberPoint))
                .willReturn(memberPoint);

        MemberPointHistory history = MemberPointHistory.createUseHistory(ANY_MEMBER_ID, amount);
        given(memberPointHistoryRepository.save(any(MemberPointHistory.class)))
                .willReturn(history);

        // when
        var result = memberPointService.charge(command);

        // then
        assertThat(result.getBalance()).isEqualTo(expectBalance);
        verify(memberPointRepository, times(1)).save(memberPoint);
        verify(memberPointHistoryRepository, times(1)).save(any(MemberPointHistory.class));
    }



}
