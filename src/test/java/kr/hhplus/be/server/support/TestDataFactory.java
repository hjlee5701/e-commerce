package kr.hhplus.be.server.support;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

@Component
public class TestDataFactory {

    private static final Random random = new Random();

    private int randomNumber(int start, int end) {
        return random.nextInt(end) + start;
    }

    private LocalDateTime createRandomDate() {
        return LocalDateTime.now().minusDays(randomNumber(1, 200));
    }


    public Member createMember() {
        return Instancio.of(Member.class)
                .ignore(field(Member::getId))
                .set(field("userId"), "Member-#{random.number()}")
                .set(field("regAt"), createRandomDate())
                .create();
    }

    public List<Product> createProducts(int endNum) {
        return Instancio.of(Product.class)
                .ignore(field(Product::getId))
                .set(field("title"), "Title-#{random.number()}")
                .set(field("price"), BigDecimal.valueOf(randomNumber(10000, 500000)))
                .set(field("quantity"), randomNumber(100, 10000))
                .stream()
                .limit(endNum)
                .toList();
    }

    private List<OrderCommand.ItemCreate> createOrderItemCommand(List<Product> products) {
        return products.stream()
                .map(product -> new OrderCommand.ItemCreate(
                        product.getId(),
                        product.getTitle(),
                        product.getPrice(),
                        product.getQuantity()
                ))
                .toList();
    }

    private Map<Long, Integer> createOrderProductMap(List<Product> products) {
        return products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        p -> randomNumber(1, 10)
                ));
    }


    public Order createOrderPaidThreeDaysAgo(Member member, LocalDateTime now, List<Product> products) {
        Order order = Instancio.of(Order.class)
                .ignore(field(Order::getId))
                .set(field("member"), member)
                .set(field("totalAmount"), BigDecimal.valueOf(randomNumber(10000, 500000)))
                .set(field("status"), OrderStatus.PAID)
                .set(field("orderedAt"), now.minusDays(3))
                .set(field("orderItems"), new ArrayList<>()) // 초기화만
                .create();

        // 2. OrderCommand.ItemCreate DTO 리스트 생성
        List<OrderCommand.ItemCreate> itemCommand = createOrderItemCommand(products);

        // 3. 상품 ID별 주문 수량 맵 구성
        Map<Long, Integer> orderProductMap = createOrderProductMap(products);

        // 4. 연관관계 구성 (orderItems 내부 세팅 포함)
        order.addItems(itemCommand, orderProductMap);
        return order;
    }


    public Order createPaidOrder(Member member, LocalDateTime now, Product product, int minusDays) {
        Order order = Instancio.of(Order.class)
                .ignore(field(Order::getId))
                .set(field("member"), member)
                .set(field("totalAmount"), BigDecimal.valueOf(randomNumber(10000, 500000)))
                .set(field("status"), OrderStatus.PAID)
                .set(field("orderedAt"), now.minusDays(minusDays))
                .set(field("orderItems"), null) // 나중에 수동 세팅
                .create();

        // 2. OrderCommand.ItemCreate DTO 리스트 생성
        List<OrderCommand.ItemCreate> itemCommand = createOrderItemCommand(List.of(product));

        // 3. 상품 ID별 주문 수량 맵 구성
        Map<Long, Integer> orderProductMap = createOrderProductMap(List.of(product));

        // 4. 연관관계 구성 (orderItems 내부 세팅 포함)
        order.addItems(itemCommand, orderProductMap);
        return order;

    }
}
