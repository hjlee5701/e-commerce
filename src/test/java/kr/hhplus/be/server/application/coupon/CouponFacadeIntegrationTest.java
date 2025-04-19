package kr.hhplus.be.server.application.coupon;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Testcontainers
@Transactional
public class CouponFacadeIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private CouponFacade facade;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private Coupon coupon;
    @BeforeEach
    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        coupon = new Coupon(null, "선착순 쿠폰", 100, 100, BigDecimal.TEN, CouponStatus.ACTIVE, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        entityManager.flush();
    }

    @Test
    @DisplayName("통합 테스트 - 쿠폰 발급 성공할 경우 발급 쿠폰은 저장되면 남은 수량은 차감된다.")
    void 쿠폰_발급_성공시_저장_및_수량_차감된다() {
        // given
        int remainingQuantity = coupon.getRemainingQuantity();
        CouponCriteria.Issue criteria = CouponCriteria.Issue.of(coupon.getId(), member.getId());

        // when
        CouponResult.Issued result = facade.issue(criteria);
        entityManager.flush();

        // then
        CouponItem couponItem = couponItemRepository.findById(result.getCouponItemId())
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(couponItem).isNotNull();
        assertAll(
                () -> assertThat(couponItem.getCoupon().getId()).isEqualTo(coupon.getId()),
                () -> assertThat(couponItem.getCoupon().getTitle()).isEqualTo(coupon.getTitle()),
                () -> assertThat(couponItem.getStatus()).isEqualTo(CouponItemStatus.USABLE)
        );
        assertThat(couponItem.getCoupon().getRemainingQuantity())
                .isEqualTo(remainingQuantity - 1);
    }

}
