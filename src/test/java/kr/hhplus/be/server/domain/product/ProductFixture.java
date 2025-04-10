package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class ProductFixture implements TestFixture<Product> {
    private Long id = 1L;

    private String title = "상품";

    private BigDecimal price = BigDecimal.ZERO;

    private int quantity = 0;

    @Override
    public Product create() {
        Product entity = new Product();
        FixtureReflectionUtils.reflect(entity, this);
        id += 1;
        title = title+id;
        price = price.add(BigDecimal.TEN);
        quantity += 10;
        return entity;
    }


    public Product createWithStock(int quantity) {
        Product entity = new Product();
        FixtureReflectionUtils.reflect(entity, this);
        this.quantity = quantity;
        return entity;
    }
}
