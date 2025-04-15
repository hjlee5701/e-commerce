package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderedAt;

    public static Order create(Long memberId) {
        requireNonNull(memberId);
        return new Order(null, Member.referenceById(memberId), BigDecimal.ZERO, new ArrayList<>(), OrderStatus.PENDING, null);
    }

    public void addItems(List<OrderCommand.ItemCreate> products, Map<Long, Integer> orderProductMap) {
        this.orderItems = products.stream()
                .map(product -> {
                    int quantity = orderProductMap.getOrDefault(product.getProductId(), 0);
                    if (quantity <= 0) {
                        throw new ECommerceException(OrderErrorCode.INVALID_ORDER_QUANTITY);
                    }
                    return OrderItem.create(this, product, quantity);
                })
                .toList();
    }

    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void applyPayment(BigDecimal finalAmount) {
        updateFinalAmount(finalAmount);
        markAsPaid();
    }

    private void updateFinalAmount(BigDecimal finalAmount) {
        this.totalAmount = finalAmount.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : finalAmount;
    }



    private void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new ECommerceException(OrderErrorCode.NOT_PENDING_ORDER, status.name());
        }
        status = OrderStatus.PAID;
    }

    public void checkOrderer(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new ECommerceException(OrderErrorCode.FORBIDDEN_ORDER_ACCESS);

        }
    }
}
