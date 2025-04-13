package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.Member;
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


    public void completePayment(BigDecimal finalAmount) {
        updateFinalAmount(finalAmount);
        markAsPaid();
    }

    public void updateFinalAmount(BigDecimal finalAmount) {
        this.totalAmount = finalAmount.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : finalAmount;
    }



    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new NotPendingOrderException(status.name());
        }
        status = OrderStatus.PAID;
    }
}
