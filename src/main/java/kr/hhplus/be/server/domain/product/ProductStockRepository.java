package kr.hhplus.be.server.domain.product;

import java.util.List;

public interface ProductStockRepository {
    ProductStock save(ProductStock productStock);

    List<ProductStock> saveAll(Iterable<ProductStock> stocks);
}
