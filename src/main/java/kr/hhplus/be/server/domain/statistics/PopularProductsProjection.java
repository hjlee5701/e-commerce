package kr.hhplus.be.server.domain.statistics;

import java.math.BigDecimal;

public record PopularProductsProjection(Long productId, String title, BigDecimal price, Long totalSoldQuantity) {
}
