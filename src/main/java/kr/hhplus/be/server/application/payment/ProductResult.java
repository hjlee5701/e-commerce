package kr.hhplus.be.server.application.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class ProductResult {

    @Getter
    @AllArgsConstructor
    public static class Popular {
        private Long productId;
        private String title;
        private BigDecimal price;
        private Integer quantity;
    }
}
