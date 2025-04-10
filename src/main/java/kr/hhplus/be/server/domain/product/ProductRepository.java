package kr.hhplus.be.server.domain.product;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository {
    Optional<Product> findById(Long productId);

    List<Product> findAllById(Iterable<Long> productIds);
}
