package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private int quantity;

    public static OrderItem create(Order order, OrderCommand.ItemCreate product, Integer orderQuantity) {
        if (orderQuantity == null || orderQuantity <= 0) {
            throw new ECommerceException(OrderErrorCode.INVALID_ORDER_QUANTITY);
        }

        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(orderQuantity));

        return new OrderItem(
                null,
                product.getTitle(),
                order,
                Product.referenceById(product.getProductId()),
                unitPrice,
                totalPrice,
                orderQuantity
        );
    }
}

