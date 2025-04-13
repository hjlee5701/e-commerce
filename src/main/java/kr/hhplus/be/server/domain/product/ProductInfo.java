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

    @Getter
    @AllArgsConstructor
    public static class Decreased {
        private BigDecimal totalAmount;
        private List<ItemDecreased> items;

        public static Decreased of(BigDecimal totalAmount, List<ItemDecreased> items) {
            return new Decreased(totalAmount, items);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ItemDecreased {
        private Product product;
        private BigDecimal price;
        private int orderQuantity;

        public static ItemDecreased of(Product product, int orderQuantity) {
            return new ItemDecreased(product, product.getPrice(), orderQuantity);

        }
    }
}
