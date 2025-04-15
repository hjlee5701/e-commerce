package kr.hhplus.be.server.application.memberPoint;

import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.CouponItemRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointHistoryRepository;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

@SpringBootTest
@Testcontainers
@Transactional
public class MemberPointFacadeIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberPointHistoryRepository memberPointHistoryRepository;

    @Autowired
    private MemberPointFacade facade;

    @Test
    void 회원_충전_성공() {

        // givne
        Member member = new Member(null, "test", LocalDateTime.now());
        memberRepository.save(member);

        MemberPointCriteria.Charge criteria = new MemberPointCriteria.Charge(member.getMemberId(), amount);
        MemberPointResult.ChargeBalance result = facade.charge(criteria);

        MemberPoint memberPoint = memberPointRepository.findByMemberId(member.getId())
                .orElse(null);



    }

}
