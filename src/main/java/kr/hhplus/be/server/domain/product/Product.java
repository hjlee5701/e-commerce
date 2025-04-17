package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String title;

    private BigDecimal price;

    private int quantity;

    private Product(Long productId) {
        this.id = productId;
    }

    public static Product referenceById(Long productId) {
        return new Product(productId);
    }

}
