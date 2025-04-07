package kr.hhplus.be.server.domain.memberPoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberPointService {

    private final MemberPointRepository memberPointRepository;

    public MemberPointInfo.Balance charge(MemberPointCommand.Charge command) {
        Optional<MemberPoint> mayMemberPoint = memberPointRepository.findByMemberId(command.getMemberId());
        MemberPoint memberPoint = mayMemberPoint.orElseGet(() -> MemberPointFactory.createInitialPoint(command.getMember()));
        memberPoint.charge(command);
        memberPointRepository.save(memberPoint);
        return MemberPointInfo.Balance.of(memberPoint);
    }
}
