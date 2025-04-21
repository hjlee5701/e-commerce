package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsFacade;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsRepository;
import kr.hhplus.be.server.domain.statistics.PopularProductsProjection;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
public class OrderStatisticsServiceIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatisticsRepository orderStatisticsRepository;

    @Autowired
    private OrderStatisticsFacade facade;

    private List<Product> products;
    private Member member;

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;

    void setUp() {

        member = factory.createMember();
        testDataManager.persist(List.of(member));

        products = factory.createProducts(6);
        testDataManager.persist(products);
    }


    @Test
    @DisplayName("통합 테스트 - 5개의 인기 판매 상품을 조회한다.")
    void 다섯개의_인기_상품만_조회(){
        // given
        setUp();

        // 통계에 들어갈 6개 판매 완료 주문을 생성
        for (int i = 0; i < 6; i++) {
            Order order = factory.createOrderPaidThreeDaysAgo(member, FIXED_NOW, products);
            testDataManager.persist(order);
            testDataManager.persist(order.getOrderItems());
        }
        testDataManager.flushAndClear();

        // when
        facade.aggregateOrderStatistics(FIXED_NOW);
        testDataManager.flushAndClear();

        Pageable pageable = PageRequest.of(0, 5);

        // then
        List<Order> orders = orderRepository.getPaidOrderByDate(FIXED_NOW.minusDays(3), FIXED_NOW, OrderStatus.PAID);
        assertThat(orders.size()).isEqualTo(6);
        
        Page<PopularProductsProjection> popularPage = orderStatisticsRepository.findPopularProductsForDateRange(FIXED_NOW.minusDays(3), FIXED_NOW, pageable);
        List<PopularProductsProjection> populars = popularPage.getContent();

        assertThat(populars.size()).isEqualTo(5);
    }
}
