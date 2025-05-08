package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
public class OrderStatisticsFacadeIntegrationTest {

    @Autowired
    private OrderStatisticsRepository orderStatisticsRepository;

    @Autowired
    private OrderJpaRepository orderRepository;
    @Autowired
    private OrderStatisticsFacade facade;

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;


    private Member member;
    private Product product;


    void setUp() {
        List<Product> products = factory.createProducts(1);
        testDataManager.persist(products);
        product = products.get(0);

        member = factory.createMember();
        testDataManager.persist(List.of(member));
    }

    @Test
    @DisplayName("통합 테스트 - 지정된 날짜의 결제 완료 주문을 기반으로 통계를 집계한다")
    void 결제_완료_주문을_기반한_통계_집계() {
        // given
        setUp();

        // 동일 상품 2개 주문
        int aggregateQuantity = 0;
        for (int i = 0; i < 2; i++) {
            Order order = factory.createToDayPaidOrder(member, FIXED_NOW, List.of(product));
            aggregateQuantity += order.getOrderItems().get(0).getQuantity();
            testDataManager.persist(order);
            testDataManager.persist(order.getOrderItems());
        }
        testDataManager.flushAndClear();
        LocalDate startDate = FIXED_NOW.toLocalDate();
        LocalDate endDate = startDate.plusDays(1);

        // when
        facade.aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate.of(endDate));
        testDataManager.flushAndClear();

        // then
        List<OrderStatistics> stats = orderStatisticsRepository.getByProductIdsAndDate(
                startDate, endDate, Set.of(product.getId())
        );
        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getTotalSoldQuantity()).isEqualTo(aggregateQuantity);
    }


    @Test
    @DisplayName("통합 테스트 - 통계가 존재하지 않는 경우 새로운 통계를 생성한다")
    void 기존_통계가_없는_경우_새로운_통계_생성() {
        // given
        setUp();
        // 주문 생성
        Order order = factory.createToDayPaidOrder(member, FIXED_NOW, List.of(product));
        testDataManager.persist(order);
        testDataManager.persist(order.getOrderItems());
        testDataManager.flushAndClear();

        int quantity = order.getOrderItems().get(0).getQuantity();

        LocalDate startDate = FIXED_NOW.toLocalDate();
        LocalDate endDate = startDate.plusDays(1);

        List<OrderStatistics> before = orderStatisticsRepository.getByProductIdsAndDate(
                startDate,
                endDate,
                Set.of(product.getId())
        );
        assertThat(before).isEmpty();

        // when
        facade.aggregateOrderStatistics(OrderStatisticsCriteria.Aggregate.of(endDate));
        testDataManager.flushAndClear();

        // then
        List<OrderStatistics> stats = orderStatisticsRepository.getByProductIdsAndDate(
                startDate,
                endDate,
                Set.of(product.getId())
        );

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getTotalSoldQuantity()).isEqualTo(quantity);
    }

}
