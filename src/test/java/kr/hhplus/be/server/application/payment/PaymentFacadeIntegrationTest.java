package kr.hhplus.be.server.application.payment;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@Transactional
public class PaymentFacadeIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private Order order;
    private Coupon coupon;
    private CouponItem couponItem;
    private MemberPoint memberPoint;

    @BeforeEach
    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        memberPoint = new MemberPoint(null, Member.referenceById(member.getId()), MemberPointPolicy.MAX_BALANCE_AMOUNT);
        memberPointRepository.save(memberPoint);

        order = new Order(null, member, BigDecimal.ZERO, new ArrayList<>(), OrderStatus.PENDING, LocalDateTime.now());
        orderRepository.save(order);

        coupon = new Coupon(null, "선착순 쿠폰", 100, 100, BigDecimal.TEN, CouponStatus.ACTIVE, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        couponItem = new CouponItem(null, member, coupon, CouponItemStatus.USABLE);
        couponItemRepository.save(couponItem);
        entityManager.flush();
    }

    @Test
    @DisplayName("통합 테스트 - 결제 성공할 경우 결제 생성 및 쿠폰/결제/주문 상태가 변경된다.")
    void 결제_생성_성공() {
        // given
        BigDecimal originalBalance = memberPoint.getBalance();
        BigDecimal originalTotalAmount = order.getTotalAmount();
        BigDecimal discountAmount = coupon.getDiscountAmount();

        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(order.getId(), couponItem.getId(), member.getId());

        // when
        PaymentResult.Paid result = paymentFacade.createPayment(criteria);
        entityManager.flush();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(order.getId());

        // 결제 저장 확인
        Payment savedPayment = paymentRepository.findById(result.getPaymentId())
                .orElse(null);

        assertThat(savedPayment).isNotNull();
        var expectedFinalAmount = originalTotalAmount.subtract(discountAmount).max(BigDecimal.ZERO);

        assertAll("결제 검증",
                () -> assertEquals(0, expectedFinalAmount.compareTo(savedPayment.getFinalAmount())),
                () -> assertEquals(0, originalBalance.subtract(expectedFinalAmount).compareTo(memberPoint.getBalance())),
                () -> assertThat(PaymentStatus.COMPLETED).isEqualTo(savedPayment.getStatus())
        );
        assertAll("주문 검증",
                () -> assertEquals(0, savedPayment.getFinalAmount().compareTo(savedPayment.getOrder().getTotalAmount())),
                () -> assertEquals(OrderStatus.PAID, savedPayment.getOrder().getStatus())
        );
        assertAll("쿠폰 검증",
                () -> assertEquals(savedPayment.getCouponItem().getId(), couponItem.getId()),
                () -> assertEquals(CouponItemStatus.USED, savedPayment.getCouponItem().getStatus())
        );
    }
}
