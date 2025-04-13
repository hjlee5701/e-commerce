package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
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

    public void decrease(Integer decreaseQuantity) {
        if (decreaseQuantity <= 0) {
            throw new ECommerceException(ProductErrorCode.INVALID_DECREASE_QUANTITY);
        }
        if (this.quantity < decreaseQuantity) {
            throw new ECommerceException(ProductErrorCode.INSUFFICIENT_STOCK, title);
        }
        quantity -= decreaseQuantity;
    }
}
