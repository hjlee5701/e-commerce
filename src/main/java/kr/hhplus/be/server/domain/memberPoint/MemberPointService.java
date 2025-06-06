package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.shared.code.MemberPointErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberPointService {

    private final MemberPointRepository memberPointRepository;
    private final MemberPointHistoryRepository memberPointHistoryRepository;

    public MemberPointInfo.Balance charge(MemberPointCommand.Charge command) {
        // 충전 금액 검증
        if (command.getAmount().compareTo(MemberPointPolicy.MAX_CHARGE_AMOUNT) >= 0) {
            throw new ECommerceException(MemberPointErrorCode.INVALID_AMOUNT);
        }
        // 금액 조회 (금액 없을 경우, 초기화)
        MemberPoint memberPoint = memberPointRepository.findByMemberIdForUpdate(command.getMemberId())
                .orElseGet(() -> MemberPoint.createInitialPoint(command.getMemberId()));

        // 충전
        memberPoint.charge(command.getAmount());
        MemberPoint savedMemberPoint = memberPointRepository.save(memberPoint);

        memberPointHistoryRepository.save(MemberPointHistory.createChargeHistory(command.getMemberId(), command.getAmount()));
        return MemberPointInfo.Balance.of(savedMemberPoint);
    }

    public MemberPointInfo.Balance getBalance(MemberCommand.Find command) {
        // 잔액 조회
        MemberPoint memberPoint = memberPointRepository.findByMemberId(command.getMemberId())
                .orElseThrow(() -> new ECommerceException(MemberPointErrorCode.MEMBER_POINT_NOT_FOUND));

        return MemberPointInfo.Balance.of(memberPoint);
    }

    public void use(MemberPointCommand.Use command) {
        MemberPoint memberPoint = memberPointRepository.findByMemberIdForUpdate(command.getMemberId())
                .orElseThrow(() -> new ECommerceException(MemberPointErrorCode.MEMBER_POINT_NOT_FOUND));

        memberPoint.use(command.getAmount());
        memberPointHistoryRepository.save(MemberPointHistory.createUseHistory(command.getMemberId(), command.getAmount()));
    }
}
