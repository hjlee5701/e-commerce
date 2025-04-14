package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {
}
