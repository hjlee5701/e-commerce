package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProductStockFixture implements TestFixture<ProductStock> {
    private Long id = 1L;

    private Product product = new ProductFixture().create();

    private int quantity = 100;

    @Override
    public ProductStock create() {
        ProductStock entity = new ProductStock();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public ProductStock withId(Long id) {
        ProductStock entity = new ProductStock();
        this.id = id;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }


    public ProductStock withIdAndQuantity(Long id, int quantity) {
        ProductStock entity = new ProductStock();
        this.id = id;
        this.quantity = quantity;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}
