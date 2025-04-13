package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.exception.InvalidAmountException;
import kr.hhplus.be.server.domain.memberPoint.exception.MemberPointNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberPointService {

    private final MemberPointRepository memberPointRepository;

    public MemberPointInfo.Balance charge(MemberPointCommand.Charge command) {
        // 충전 금액 검증
        if (command.getAmount().compareTo(MemberPointPolicy.MAX_CHARGE_AMOUNT) >= 0) {
            throw new InvalidAmountException();
        }
        // 금액 조회
        Optional<MemberPoint> mayMemberPoint = memberPointRepository.findByMemberId(command.getMemberId());

        // 금액 없을 경우, 초기화
        MemberPoint memberPoint = mayMemberPoint.orElseGet(
                () -> MemberPointFactory.createInitialPoint(command.getMember())
        );

        // 충전
        memberPoint.charge(command);
        memberPointRepository.save(memberPoint);

        return MemberPointInfo.Balance.of(memberPoint);
    }

    public MemberPointInfo.Balance getBalance(Long memberId) {
        // 금액 조회
        MemberPoint memberPoint = memberPointRepository.findByMemberId(memberId)
                .orElseThrow(MemberPointNotFoundException::new);

        return MemberPointInfo.Balance.of(memberPoint);
    }

    public void use(MemberPointCommand.Use command) {
        MemberPoint memberPoint = memberPointRepository.findByMemberId(command.getMemberId())
                .orElseThrow(MemberPointNotFoundException::new);

        memberPoint.use(command.getAmount());
    }
}
