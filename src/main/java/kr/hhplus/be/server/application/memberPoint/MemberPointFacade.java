package kr.hhplus.be.server.application.memberPoint;

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

        // 존재하는 회원
        memberService.findMemberById(criteria.toFindMemberCommand());

        MemberPointCommand.Charge command = criteria.toChargeCommand();

        // 충전
        MemberPointInfo.Balance info = memberPointService.charge(command);

        // 충전 이력
        memberPointHistoryService.createChargeHistory(command);

        return new MemberPointResult.ChargeBalance(criteria.getMemberId(), info.getBalance());
    }

}

