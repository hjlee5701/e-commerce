package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.application.memberPoint.MemberPointCriteria;
import kr.hhplus.be.server.application.memberPoint.MemberPointFacade;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberPointFacadeTest {

    @Mock
    private MemberService memberService;
    @Mock
    private MemberPointService memberPointService;
    @Mock
    private MemberPointHistoryService memberPointHistoryService;

    @InjectMocks
    private MemberPointFacade facade;


    @DisplayName("충전 facade 의 서비스 호출 순서를 검증한다.")
    @Test
    void 서비스_호출_순서_검증() {
        // given
        var amount = BigDecimal.valueOf(1000);
        var criteria = new MemberPointCriteria.Charge(ANY_MEMBER_ID, amount);

        given(memberService.findMemberById(criteria.toFindMemberCommand())).willReturn(mock(MemberInfo.Detail.class));
        given(memberPointService.charge(any()))
                .willReturn(new MemberPointInfo.Balance(amount));

        willDoNothing().given(memberPointHistoryService).createChargeHistory(any());

        // when
        facade.charge(criteria);

        // then
        InOrder order = Mockito.inOrder(memberService, memberPointService, memberPointHistoryService);

        order.verify(memberService).findMemberById(criteria.toFindMemberCommand());
        order.verify(memberPointService).charge(any(MemberPointCommand.Charge.class));
        order.verify(memberPointHistoryService).createChargeHistory(any(MemberPointCommand.Charge.class));
    }

    @DisplayName("회원이 조회 실패시 충전 서비스와 충전 이력 서비스는 동작하지 않는다.")
    @Test
    void 회원_조회_실패시_이후_서비스_동작_안함() {
        // given
        var criteria = new MemberPointCriteria.Charge(ANY_MEMBER_ID, BigDecimal.valueOf(1000));

        given(memberService.findMemberById(criteria.toFindMemberCommand())).willThrow(kr.hhplus.be.server.domain.common.ECommerceException.class);

        // when
        assertThatThrownBy(() -> facade.charge(criteria)).isInstanceOf(kr.hhplus.be.server.domain.common.ECommerceException.class);

        // then
        verify(memberPointService, never()).charge(any(MemberPointCommand.Charge.class));
        verify(memberPointHistoryService, never()).createChargeHistory(any(MemberPointCommand.Charge.class));

    }

    @DisplayName("충전 실패시 충전 이력 서비스는 동작하지 않는다.")
    @Test
    void 충전_실패시_충전_이력_서비스_동작_안함() {
        // given
        var criteria = new MemberPointCriteria.Charge(ANY_MEMBER_ID, BigDecimal.valueOf(1000));

        given(memberService.findMemberById(criteria.toFindMemberCommand())).willReturn(mock(MemberInfo.Detail.class));
        given(memberPointService.charge(any(MemberPointCommand.Charge.class))).willThrow(ECommerceException.class);

        // when
        assertThatThrownBy(() -> facade.charge(criteria)).isInstanceOf(ECommerceException.class);

        // then
        verify(memberPointHistoryService, never()).createChargeHistory(any(MemberPointCommand.Charge.class));

    }

    @DisplayName("충전 성공시 충전 이력이 저장된다.")
    @Test
    void 충전_성공시_이력_저장_성공() {
        // given
        var criteria = new MemberPointCriteria.Charge(ANY_MEMBER_ID, BigDecimal.valueOf(1000));
        var info = MemberPointInfo.Balance.of(new MemberPointFixture().create());

        given(memberService.findMemberById(criteria.toFindMemberCommand())).willReturn(mock(MemberInfo.Detail.class));
        given(memberPointService.charge(criteria.toChargeCommand())).willReturn(info);
        willDoNothing().given(memberPointHistoryService).createChargeHistory(criteria.toChargeCommand());

        // when
        facade.charge(criteria);

        // then
        verify(memberPointHistoryService, times(1)).createChargeHistory(any(MemberPointCommand.Charge.class));
    }


}
