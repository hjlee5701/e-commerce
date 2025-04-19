package kr.hhplus.be.server.domain.product;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
public class ProductStockServiceIntegrationTest {
    @Autowired
    private ProductStockService productStockService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private EntityManager entityManager;

    private Product productA;
    private ProductStock stockA;
    private Product productB;
    private ProductStock stockB;

    void setUp() {
        int quantity = 100;
        productA = new Product(null, "상품A", BigDecimal.valueOf(10000), quantity);
        productB = new Product(null, "상품B", BigDecimal.valueOf(10000), quantity);
        productRepository.saveAll(List.of(productA, productB));

        stockA = new ProductStock(null, productA, quantity);
        stockB = new ProductStock(null, productB, quantity);
        productStockRepository.saveAll(List.of(stockA, stockB));
        cleanUp();
    }

    void cleanUp() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("통합 테스트 - 상품 수량 차감에 성공한다.")
    void 상품_수량_차감_성공() {
        // given
        setUp();
        int originalStockA = productA.getQuantity();
        int originalStockB = productB.getQuantity();
        int orderQuantityA = 1;
        int orderQuantityB = 2;
        Map<Long, Integer> productMap = Map.of(productA.getId(), orderQuantityA, productB.getId(), orderQuantityB);

        ProductStockCommand.Decrease command = new ProductStockCommand.Decrease(productMap);

        // when
        productStockService.decreaseStock(command);
        cleanUp();

        // then
        stockA = productStockRepository.findByProductId(productA.getId())
                .orElseThrow(() -> new ECommerceException(ProductErrorCode.PRODUCT_STOCK_NOT_FOUND));
        stockB = productStockRepository.findByProductId(productB.getId())
                .orElseThrow(() -> new ECommerceException(ProductErrorCode.PRODUCT_STOCK_NOT_FOUND));

        assertEquals(originalStockA-orderQuantityA, stockA.getQuantity());
        assertEquals(originalStockB-orderQuantityB, stockB.getQuantity());
    }

    @Test
    @DisplayName("통합 테스트 - 재고 부족으로 상품 수량 차감에 실패한다.")
    void 상품_재고_없을_때_예외처리() {
        // given
        setUp();
        int overQuantity = productA.getQuantity() + 100;
        Map<Long, Integer> productMap = Map.of(productA.getId(), overQuantity);

        ProductStockCommand.Decrease command = new ProductStockCommand.Decrease(productMap);

        // when & then
        ECommerceException exception = assertThrows(ECommerceException.class, () -> {
            productStockService.decreaseStock(command);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.INSUFFICIENT_STOCK.getCode());
    }
}
