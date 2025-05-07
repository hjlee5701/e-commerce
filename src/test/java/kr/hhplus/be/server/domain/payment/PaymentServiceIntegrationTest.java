package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class PaymentServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private EntityManager entityManager;

    private Order order;
    private Member member;
    private Coupon coupon;

    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        order = new Order(null, member, BigDecimal.ZERO, new ArrayList<>(), OrderStatus.PENDING, LocalDateTime.now());
        orderRepository.save(order);

        coupon = new Coupon(null, "선착순 쿠폰", 100, 100, BigDecimal.TEN, CouponStatus.ACTIVE, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        cleanUp();
    }

    void cleanUp() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("통합 테스트 - 결제가 성공할 경우 결제 생성 및 쿠폰 적용 처리가 된다.")
    void 결제_생성_및_쿠폰_적용_성공() {
        // given
        setUp();
        CouponItem couponItem = new CouponItem(null, member, coupon, CouponItemStatus.USABLE);
        couponItemRepository.save(couponItem);
        PaymentCommand.Pay command = new PaymentCommand.Pay(order, member.getId(), couponItem);

        // when
        Payment payment = paymentService.pay(command);

        // then
        BigDecimal expectedFinalAmount = order.getTotalAmount().subtract(coupon.getDiscountAmount()).max(BigDecimal.ZERO);
        cleanUp();

        Payment savedPayment = paymentRepository.findById(payment.getId())
                .orElse(null);
        assertThat(savedPayment).isNotNull();
        assertAll(
                () -> assertEquals(expectedFinalAmount, payment.getFinalAmount()),
                () -> assertEquals(order, payment.getOrder()),
                () -> assertEquals(payment.getCouponItem().getId(), savedPayment.getCouponItem().getId()),
                () -> assertEquals(CouponItemStatus.USED, savedPayment.getCouponItem().getStatus())
        );
    }

    @Test
    @DisplayName("통합 테스트 - 쿠폰 없이 결제가 성공할 경우 할인 없는 결제가 생성된다.")
    void 쿠폰_적용_없이_결제_성공() {
        // given
        setUp();
        PaymentCommand.Pay command = new PaymentCommand.Pay(order, member.getId(), null);

        // when
        Payment payment = paymentService.pay(command);
        cleanUp();

        // then
        Payment savedPayment = paymentRepository.findById(payment.getId())
                .orElse(null);

        assertThat(savedPayment).isNotNull();
        assertAll(
                () -> assertEquals(order.getTotalAmount(), payment.getFinalAmount()),
                () -> assertEquals(order.getId(), payment.getOrder().getId()),
                () -> assertNull(savedPayment.getCouponItem())
        );
    }
}

