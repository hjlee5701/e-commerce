package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long productId);

    List<Product> findAllById(Iterable<Long> productIds);
}
