package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
public class ProductInfo {

    @Getter
    @AllArgsConstructor
    public static class Detail {
        private Long productId;
        private String title;
        private BigDecimal price;
        private Integer quantity;


        public static Detail of(Product product) {
            return new Detail(product.getId(), product.getTitle(), product.getPrice(), product.getQuantity());
        }
    }


}
