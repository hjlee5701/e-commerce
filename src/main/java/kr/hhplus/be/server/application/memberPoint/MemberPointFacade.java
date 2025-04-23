package kr.hhplus.be.server.application.memberPoint;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointInfo;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberPointFacade {

    private final MemberService memberService;
    private final MemberPointService memberPointService;

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100),
            recover = "recoverCharge"
    )
    @Transactional
    public MemberPointResult.ChargeBalance charge(MemberPointCriteria.Charge criteria) {

        // 존재하는 회원
        memberService.findMemberById(criteria.toFindMemberCommand());

        MemberPointCommand.Charge command = criteria.toChargeCommand();

        // 충전 & 충전 이력
        MemberPointInfo.Balance info = memberPointService.charge(command);

        return new MemberPointResult.ChargeBalance(criteria.getMemberId(), info.getBalance());
    }

    @Recover
    public MemberPointResult.ChargeBalance recoverCharge(Exception e, MemberPointCriteria.Charge criteria) {
        throw new ECommerceException(MemberPointErrorCode.CONCURRENCY_CHARGE);
    }
}

