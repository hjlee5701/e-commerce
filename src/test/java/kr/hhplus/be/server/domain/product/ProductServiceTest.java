package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductStockRepository productStockRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품ID set 에 존재하지 않는 ID 로 상품 수량 차감에 실패한다.")
    void 상품ID_모음_중_존재하지_않는_ID로_수량_차감_실퍠() {
        // given
        ProductFixture fixture = new ProductFixture();

        Product product1 = fixture.create();
        Product product2 = fixture.create();
        Map<Long, Integer> productMap = Map.of(product1.getId(), product1.getQuantity(), product2.getId(), product2.getQuantity());

        given(productRepository.findAllById(productMap.keySet()))
                .willReturn(List.of(product1));

        // when
        ProductCommand.Decrease command = new ProductCommand.Decrease(productMap);

        // then
        assertThatThrownBy(() -> productService.decreaseStock(command))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고 부족으로 상품 수량 차감에 실패하면 출고 이력 저장에 실패한다.")
    void 상품_수량_차감_실패로_출고_이력_저장_실패() {
        // given
        ProductFixture fixture = new ProductFixture();

        int decreaseQuantity = 10;
        Product product = fixture.createWithStock(5);
        Map<Long, Integer> productMap = Map.of(product.getId(), decreaseQuantity);

        given(productRepository.findAllById(productMap.keySet()))
                .willReturn(List.of(product));


        ProductCommand.Decrease command = new ProductCommand.Decrease(productMap);
        // when
        assertThatThrownBy(() -> productService.decreaseStock(command))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(String.format(ProductErrorCode.INSUFFICIENT_STOCK.getMessage(), product.getTitle()));

        // then
        verify(productStockRepository, never()).save(any());
    }
    
    
    @Test
    @DisplayName("상품 수량 차감에 성공하면 출고 이력도 저장된다.")
    void 상품_수량_차감_후_출고_이력이_저장() {
        // given
        ProductFixture fixture = new ProductFixture();

        Product product1 = fixture.createWithStock(10);
        Product product2 = fixture.createWithStock(10);
        Map<Long, Integer> productMap = Map.of(product1.getId(), product1.getQuantity(), product2.getId(), product2.getQuantity());

        given(productRepository.findAllById(productMap.keySet()))
                .willReturn(List.of(product1, product2));


        ProductStock productStock1 = ProductFactory.createOutBoundStock(product1, product1.getQuantity());
        ProductStock productStock2 = ProductFactory.createOutBoundStock(product2, product2.getQuantity());
        given(productStockRepository.save(any()))
                .willReturn(productStock1)
                .willReturn(productStock2);

        ProductCommand.Decrease command = new ProductCommand.Decrease(productMap);
        // when
        productService.decreaseStock(command);

        // then
        verify(productStockRepository, times(2)).save(any());
    }


    @Test
    @DisplayName("수량이 차감된 모든 상품의 가격의 합이 반환값에 포함된다.")
    void 수량이_차감된_모든_상품의_가격_합이_반환() {
        // given
        ProductFixture fixture = new ProductFixture();

        Product product1 = fixture.createWithStock(10);
        Product product2 = fixture.createWithStock(10);
        Map<Long, Integer> productMap = Map.of(product1.getId(), product1.getQuantity(), product2.getId(), product2.getQuantity());

        given(productRepository.findAllById(productMap.keySet()))
                .willReturn(List.of(product1, product2));


        ProductStock productStock1 = ProductFactory.createOutBoundStock(product1, product1.getQuantity());
        ProductStock productStock2 = ProductFactory.createOutBoundStock(product2, product2.getQuantity());
        given(productStockRepository.save(any()))
                .willReturn(productStock1)
                .willReturn(productStock2);

        ProductCommand.Decrease command = new ProductCommand.Decrease(productMap);
        // when
        List<ProductInfo.Detail> info = productService.decreaseStock(command);

        // then
        assertEquals(2, info.size());
    }



}
