package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductStockRepositoryImpl implements ProductStockRepository {
    @Override
    public ProductStock save(ProductStock productStock) {
        return null;
    }
}
