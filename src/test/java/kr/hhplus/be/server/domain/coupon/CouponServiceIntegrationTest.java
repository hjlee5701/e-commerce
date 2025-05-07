package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private CouponItemRepository couponItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private Coupon coupon;


    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        coupon = new Coupon(null, "선착순 쿠폰", 100, 100, BigDecimal.TEN, CouponStatus.ACTIVE, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        cleanUp();
    }

    void cleanUp() {
        entityManager.flush();
        entityManager.clear();
    }
    
    
    @Test
    @DisplayName("통합 테스트 - 쿠폰 발급 성공할 경우 발급된 쿠폰 정보를 반환하며 잔여 수량은 감소한다.")
    void 쿠폰_발급_성공(){
        // given
        setUp();
        int remainingQuantity = coupon.getRemainingQuantity();
        CouponCommand.Issue command = new CouponCommand.Issue(coupon.getId(), member.getId());

        // when
        CouponInfo.Issued info = couponService.issue(command);
        cleanUp();

        // then
        CouponItem couponItem = couponItemRepository.findById(info.getCouponItemId())
                .orElse(null);

        assertThat(info).isNotNull();
        assertThat(couponItem).isNotNull();
        assertAll("발급된 쿠폰 정보 확인",
                () -> assertThat(couponItem.getCoupon().getId()).isEqualTo(coupon.getId()),
                () -> assertThat(couponItem.getCoupon().getTitle()).isEqualTo(coupon.getTitle()),
                () -> assertThat(couponItem.getStatus()).isEqualTo(CouponItemStatus.USABLE)
        );
        assertThat(couponItem.getCoupon().getRemainingQuantity())
                .as("잔여 수량 1 감소")
                .isEqualTo(remainingQuantity - 1);
    }

    @Test
    @DisplayName("통합 테스트 - 보유 쿠폰 조회 성공할 경우 보유한 쿠폰 리스트를 반환한다.")
    void 보유_쿠폰_조회_성공(){
        // given
        setUp();
        for (int i = 0; i < 10; i++) {
            CouponItem couponItem = coupon.issue(LocalDateTime.now(), member.getId());
            couponItemRepository.save(couponItem);
            entityManager.flush();
            entityManager.clear();
        }

        // when
        CouponCommand.Holdings command = new CouponCommand.Holdings(member.getId());
        List<CouponInfo.ItemDetail> infos = couponService.findHoldingCoupons(command);
        cleanUp();

        // then
        List<CouponItem> couponItems = couponItemRepository.findAllByMemberId(member.getId());

        assertThat(infos).isNotEmpty();
        assertThat(infos.size()).isEqualTo(10);
        assertThat(couponItems.size()).isEqualTo(10);

        Map<Long, CouponItem> couponItemMap = couponItems.stream()
                .collect(Collectors.toMap(CouponItem::getId, Function.identity()));

        for (CouponInfo.ItemDetail info : infos) {
            CouponItem item = couponItemMap.get(info.getCouponItemId());

            assertThat(item).isNotNull();

            assertAll(
                    () -> assertThat(info.getTitle()).isEqualTo(item.getCoupon().getTitle()),
                    () -> assertThat(info.getCouponItemStatus()).isEqualTo(item.getStatus().name())
            );
        }

    }

}
