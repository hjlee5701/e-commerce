package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public Optional<Product> findById(Long productId) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAllById(Iterable<Long> productIds) {
        return null;
    }
}
