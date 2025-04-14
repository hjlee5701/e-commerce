package kr.hhplus.be.server.domain.memberPoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberPointHistoryService {

    private final MemberPointHistoryRepository memberPointHistoryRepository;

    public void createChargeHistory(MemberPointCommand.Charge command) {
        memberPointHistoryRepository.save(MemberPointHistory.createChargeHistory(command.getMemberId(), command.getAmount()));
    }
}
