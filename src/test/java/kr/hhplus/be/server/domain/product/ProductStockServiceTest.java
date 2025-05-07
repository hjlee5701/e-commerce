package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductStockServiceTest {


    @Mock
    private ProductStockRepository productStockRepository;

    @InjectMocks
    private ProductStockService productStockService;

    @Test
    @DisplayName("존재하지 않는 상품ID 로 상품 수량 차감에 실패한다.")
    void 상품ID_모음_중_존재하지_않는_ID로_수량_차감_실퍠() {
        // given
        ProductStockFixture fixture = new ProductStockFixture();

        ProductStock stockA = fixture.withId(1L);
        ProductStock stockB = fixture.withId(2L);
        Map<Long, Integer> productMap = Map.of(stockA.getId(), stockA.getQuantity(), stockB.getId(), stockB.getQuantity());

        when(productStockRepository.findByProductIdForUpdate(any())).thenReturn(Optional.of(stockA)).thenReturn(Optional.empty());

        // when
        ProductStockCommand.Decrease command = new ProductStockCommand.Decrease(productMap);

        // then
        assertThatThrownBy(() -> productStockService.decreaseStock(command))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(ProductErrorCode.PRODUCT_STOCK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고 부족으로 상품 수량 차감에 실패한다.")
    void 재고_부족으로_상품_수량_차감_실패() {
        // given
        ProductStockFixture fixture = new ProductStockFixture();

        int decreaseQuantity = 10;
        ProductStock stock = fixture.withIdAndQuantity(1L, 5);
        Map<Long, Integer> productMap = Map.of(stock.getId(), decreaseQuantity);

        given(productStockRepository.findByProductIdForUpdate(stock.getId())).willReturn(Optional.of(stock));


        // when
        ProductStockCommand.Decrease command = new ProductStockCommand.Decrease(productMap);
        assertThatThrownBy(() -> productStockService.decreaseStock(command))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(String.format(ProductErrorCode.INSUFFICIENT_STOCK.getMessage()));

        // then
        verify(productStockRepository, never()).save(any());
    }


    @Test
    @DisplayName("상품 수량 차감에 성공한다.")
    void 상품_수량_차감_성공() {
        // given
        ProductStockFixture fixture = new ProductStockFixture();

        ProductStock stockA = fixture.withIdAndQuantity(1L, 5);
        ProductStock stockB = fixture.withIdAndQuantity(2L, 5);
        Map<Long, Integer> productMap = Map.of(stockA.getId(), stockA.getQuantity(), stockB.getId(), stockB.getQuantity());

        given(productStockRepository.findByProductIdForUpdate(any())).willReturn(Optional.of(stockA)).willReturn(Optional.of(stockB));

        // when
        ProductStockCommand.Decrease command = new ProductStockCommand.Decrease(productMap);
        productStockService.decreaseStock(command);

        // then
        verify(productStockRepository, times(2)).findByProductIdForUpdate(any());
        assertEquals(0, stockA.getQuantity());
        assertEquals(0, stockB.getQuantity());

    }
}
