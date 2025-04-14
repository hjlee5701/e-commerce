package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public List<Product> findAllById(Iterable<Long> productIds) {
        return productJpaRepository.findAllById(productIds);
    }
}
