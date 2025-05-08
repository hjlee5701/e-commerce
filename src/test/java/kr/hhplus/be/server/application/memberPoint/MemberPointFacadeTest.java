package kr.hhplus.be.server.application.memberPoint;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointInfo;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
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
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class MemberPointFacadeTest {

    @Mock
    private MemberService memberService;
    @Mock
    private MemberPointService memberPointService;

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

        // when
        facade.charge(criteria);

        // then
        InOrder order = Mockito.inOrder(memberService, memberPointService);

        order.verify(memberService).findMemberById(criteria.toFindMemberCommand());
        order.verify(memberPointService).charge(any(MemberPointCommand.Charge.class));
    }

    @DisplayName("충전 실패시 예외가 발생한다.")
    @Test
    void 충전_실패시_예외_발생() {
        // given
        var criteria = new MemberPointCriteria.Charge(ANY_MEMBER_ID, BigDecimal.valueOf(1000));

        given(memberService.findMemberById(criteria.toFindMemberCommand())).willReturn(mock(MemberInfo.Detail.class));
        given(memberPointService.charge(any(MemberPointCommand.Charge.class))).willThrow(ECommerceException.class);

        // when & then
        assertThatThrownBy(() -> facade.charge(criteria)).isInstanceOf(ECommerceException.class);
    }


}
