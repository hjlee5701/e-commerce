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

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderedAt;


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
}
