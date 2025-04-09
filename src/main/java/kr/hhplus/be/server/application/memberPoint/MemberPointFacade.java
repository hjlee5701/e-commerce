package kr.hhplus.be.server.application.memberPoint;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.memberPoint.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberPointFacade {

    private final MemberService memberService;
    private final MemberPointService memberPointService;
    private final MemberPointHistoryService memberPointHistoryService;

    public MemberPointResult.Balance charge(MemberPointCriteria.Charge criteria) {

        Member member = memberService.findMemberById(criteria.toFindMemberCommand());

        MemberPointCommand.Charge command = criteria.toCommand(member);
        MemberPointInfo.Balance info = memberPointService.charge(command);

        memberPointHistoryService.createChargeHistory(command);

        return new MemberPointResult.Balance(criteria.getMemberId(), info.getBalance());
    }

}

