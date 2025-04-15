package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductStockRepositoryImpl implements ProductStockRepository {

    private final ProductStockJpaRepository productStockJpaRepository;
    @Override
    public ProductStock save(ProductStock productStock) {
        return productStockJpaRepository.save(productStock);
    }

    @Override
    public List<ProductStock> saveAll(Iterable<ProductStock> stocks) {
        return productStockJpaRepository.saveAll(stocks);
    }
}
