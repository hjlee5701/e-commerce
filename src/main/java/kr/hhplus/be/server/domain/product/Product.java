package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String title;

    private BigDecimal price;

    private int quantity;

    public void decrease(Integer orderQuantity) {
        if (this.quantity < orderQuantity) {
            throw new InsufficientStockException(title);
        }
        quantity -= orderQuantity;
    }
}
