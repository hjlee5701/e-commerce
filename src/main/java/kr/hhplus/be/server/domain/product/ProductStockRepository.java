package kr.hhplus.be.server.domain.product;

import org.springframework.stereotype.Repository;


@Repository
public interface ProductStockRepository {
    ProductStock save(ProductStock productStock);
}
