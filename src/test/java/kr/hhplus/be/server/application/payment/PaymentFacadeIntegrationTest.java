package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponItemStatus;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
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
    private ProductStockRepository productStockRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;

    private Member member;
    private Order order;
    private Coupon coupon;
    private CouponItem couponItem;
    private MemberPoint memberPoint;
    private Product productA;
    private Product productB;

    private ProductStock productStockA;
    private ProductStock productStockB;

    void setUp(int orderedQuantity) {
        productA = factory.createProductByPrice(BigDecimal.valueOf(100));
        productB = factory.createProductByPrice(BigDecimal.valueOf(200));
        testDataManager.persist(List.of(productA, productB));

        LocalDateTime now = LocalDateTime.now();
        member = factory.createMember();
        memberPoint = factory.createMemberPointByBalance(member, MemberPointPolicy.MAX_BALANCE_AMOUNT);
        coupon = factory.createCoupon(10, now);

        couponItem = factory.createCouponItem(coupon, member);

        order = factory.createPendingOrderByQuantity(member, List.of(productA, productB), orderedQuantity, now);
        productStockA = factory.createProductStock(productA, 10);
        productStockB = factory.createProductStock(productB, 20);

        testDataManager.persist(List.of(member, memberPoint, coupon, couponItem, order, productStockA, productStockB));
        testDataManager.persist(order.getOrderItems());
    }

    @Test
    @DisplayName("통합 테스트 - 결제 성공할 경우 결제 생성 및 쿠폰/결제/주문 상태가 변경된다.")
    void 결제_생성_성공() {
        // given
        int orderedQuantity = 3;
        setUp(orderedQuantity);
        testDataManager.flushAndClear();

        int stockOfProductA = productStockA.getQuantity();
        int stockOfProductB = productStockB.getQuantity();


        BigDecimal originalBalance = memberPoint.getBalance();
        BigDecimal originalTotalAmount = order.getTotalAmount();
        BigDecimal discountAmount = coupon.getDiscountAmount();

        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(order.getId(), couponItem.getId(), member.getId());

        // when
        PaymentResult.Paid result = paymentFacade.createPayment(criteria);
        testDataManager.flushAndClear();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(order.getId());

        // 결제 저장 확인
        Payment savedPayment = paymentRepository.findById(result.getPaymentId()).orElse(null);
        productStockA = productStockRepository.findByProductId(productA.getId()).orElse(null);
        productStockB = productStockRepository.findByProductId(productB.getId()).orElse(null);
        memberPoint = memberPointRepository.findByMemberId(member.getId()).orElse(null);


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
        assertAll("재고 검증",
                () -> assertEquals(stockOfProductA-orderedQuantity, productStockA.getQuantity()),
                () -> assertEquals(stockOfProductB-orderedQuantity, productStockB.getQuantity())
        );
    }
}
