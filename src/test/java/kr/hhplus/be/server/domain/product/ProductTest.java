package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductTest {

    @DisplayName("차감 수량이 0보다 작거나 같을 경우 재고 차감에 실패한다")
    @ParameterizedTest(name = "수량이 {0}일 때는 예외가 발생해야 한다")
    @ValueSource(ints = {-1, 0})
    void 차감_수량이_0_보다_작거나_같을_경우_재고차감에_실패한다(int decreaseQuantity) {
        ProductFixture fixture = new ProductFixture();
        // given
        Product product = fixture.create();

        // when & then
        assertThatThrownBy(() -> product.decrease(decreaseQuantity))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(ProductErrorCode.INVALID_DECREASE_QUANTITY.getMessage());
    }


    @DisplayName("차감 수량이 상품 수량을 초과한 경우 재고 차감에 실패한다.")
    @ParameterizedTest(name = "재고보다 {0}만큼 더 차감 시 예외 발생")
    @ValueSource(ints = {100, 1000})
    void 차감_수량이_상품_수량보다_클_경우_재고차감_실패(int decreaseQuantity) {
        // given: 재고 10짜리 상품
        Product product = new ProductFixture().createWithStock(10);

        // when & then
        assertThatThrownBy(() -> product.decrease(decreaseQuantity))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(String.format(ProductErrorCode.INSUFFICIENT_STOCK.getMessage(), product.getTitle()));
    }





}
