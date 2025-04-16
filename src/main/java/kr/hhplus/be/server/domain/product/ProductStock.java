package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
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


    public void decrease(Integer decreaseQuantity) {
        if (decreaseQuantity <= 0) {
            throw new ECommerceException(ProductErrorCode.INVALID_DECREASE_QUANTITY);
        }
        if (this.quantity < decreaseQuantity) {
            throw new ECommerceException(ProductErrorCode.INSUFFICIENT_STOCK);
        }
        quantity -= decreaseQuantity;
    }

}
