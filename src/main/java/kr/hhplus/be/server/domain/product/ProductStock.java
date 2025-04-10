package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class ProductStock {
    @Id
    @GeneratedValue
    @Column(name = "PRODUCT_STOCK_ID")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private StockType type;

}
