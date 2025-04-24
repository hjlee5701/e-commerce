package kr.hhplus.be.server.domain.statistics;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.shared.code.OrderStatisticsError;
import kr.hhplus.be.server.shared.exception.ECommerceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class OrderStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATISTICS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int totalSoldQuantity;

    private LocalDate statisticsDate;

    public void aggregateQuantity(Integer soldQuantity) {
        if (soldQuantity == null || soldQuantity <= 0) {
            throw new ECommerceException(OrderStatisticsError.INVALID_SOLD_QUANTITY);
        }
        this.totalSoldQuantity += soldQuantity;
    }

    public static OrderStatistics create(Long productId, int soldQuantity, LocalDate now) {
        return new OrderStatistics(null, Product.referenceById(productId), soldQuantity, now);
    }
}
