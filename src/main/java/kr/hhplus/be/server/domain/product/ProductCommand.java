package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductCommand {

    @Getter
    @AllArgsConstructor
    public static class Detail {
        private Long productId;
    }
}
