package kr.hhplus.be.server.domain.statistics;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class OrderStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATISTICS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int totalSoldQuantity;

    @CreatedDate
    private LocalDateTime statisticsAt;


    public OrderStatistics create(Long productId, int soldQuantity) {
        return new OrderStatistics(null, Product.referenceById(productId), soldQuantity, null);
    }
}
