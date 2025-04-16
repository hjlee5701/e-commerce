package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class ProductCommand {

    @Getter
    @AllArgsConstructor
    public static class Detail {
        private Long productId;
    }

    @Getter
    @AllArgsConstructor
    public static class FindAll {
        private Set<Long> productIds;
    }
}
