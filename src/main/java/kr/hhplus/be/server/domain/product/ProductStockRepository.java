package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductStockRepository {
    ProductStock save(ProductStock productStock);

    List<ProductStock> saveAll(Iterable<ProductStock> stocks);

    Optional<ProductStock> findByProductId(Long productId);


    Optional<ProductStock> findByProductIdForUpdate(Long productId);
}
