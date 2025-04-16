package kr.hhplus.be.server.domain.statistics;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int totalSoldQuantity;

    @CreatedDate
    private LocalDateTime statisticAt;


    public OrderStatistics create(Long productId, int soldQuantity) {
        return new OrderStatistics(null, Product.referenceById(productId), soldQuantity, null);
    }
}
