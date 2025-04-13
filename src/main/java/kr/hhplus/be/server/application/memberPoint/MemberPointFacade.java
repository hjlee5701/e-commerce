package kr.hhplus.be.server.application.memberPoint;

import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointHistoryService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointInfo;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberPointFacade {

    private final MemberService memberService;
    private final MemberPointService memberPointService;
    private final MemberPointHistoryService memberPointHistoryService;

    public MemberPointResult.ChargeBalance charge(MemberPointCriteria.Charge criteria) {

        MemberInfo.Detail memberInfo = memberService.findMemberById(criteria.toFindMemberCommand());

        MemberPointCommand.Charge command = criteria.toCommand(memberInfo);
        MemberPointInfo.Balance info = memberPointService.charge(command);

        memberPointHistoryService.createChargeHistory(command);

        return new MemberPointResult.ChargeBalance(criteria.getMemberId(), info.getBalance());
    }

}

