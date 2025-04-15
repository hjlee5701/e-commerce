package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Testcontainers
@Transactional
public class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("통합 테스트 - 쿠폰 발급 성공할 경우 발급된 쿠폰 정보를 반환하며 잔여 수량은 감소한다.")
    void 쿠폰_발급_성공(){
        // given
        int remainingQuantity = 10;
        Coupon coupon = new Coupon(null, "선착순 쿠폰", 100, remainingQuantity, BigDecimal.TEN, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        CouponCommand.Issue command = new CouponCommand.Issue(coupon.getId(), 100L);

        // when
        CouponInfo.Issued info = couponService.issue(command);

        // then
        assertThat(info).isNotNull();
        assertAll("발급된 쿠폰 정보 확인",
                () -> assertThat(info.getCouponItemId()).isNotNull(),
                () -> assertThat(info.getCouponId()).isEqualTo(coupon.getId()),
                () -> assertThat(info.getTitle()).isEqualTo(coupon.getTitle()),
                () -> assertThat(info.getCouponItemStatus()).isEqualTo(CouponItemStatus.USABLE.name())
        );
        assertThat(coupon.getRemainingQuantity())
                .as("잔여 수량 1 감소")
                .isEqualTo(remainingQuantity - 1);
    }

    @Test
    @DisplayName("통합 테스트 - 보유 쿠폰 조회 성공할 경우 보유한 쿠폰 리스트를 반환한다.")
    void 보유_쿠폰_조회_성공(){
        // given
        int remainingQuantity = 10;
        Member member = new Member(null, "test", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);

        Coupon coupon = new Coupon(null, "선착순 쿠폰", 100, remainingQuantity, BigDecimal.TEN, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        for (int i = 0; i < 10; i++) {
            CouponCommand.Issue issueCommand = new CouponCommand.Issue(coupon.getId(), savedMember.getId());
            couponService.issue(issueCommand);
        }

        // when
        CouponCommand.Holdings command = new CouponCommand.Holdings(savedMember.getId());
        List<CouponInfo.ItemDetail> infos = couponService.findHoldingCoupons(command);

        // then
        assertThat(infos).isNotEmpty();
        assertThat(infos.size()).isEqualTo(10);
        for (CouponInfo.ItemDetail info : infos) {
            assertAll(
                    () -> assertThat(info.getCouponItemId()).isNotNull(),
                    () -> assertThat(info.getTitle()).isEqualTo(coupon.getTitle()),
                    () -> assertThat(info.getCouponItemStatus()).isEqualTo(CouponItemStatus.USABLE.name())
            );
        }
    }
}
