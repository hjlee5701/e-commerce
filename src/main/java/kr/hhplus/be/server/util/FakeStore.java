package kr.hhplus.be.server.util;

import kr.hhplus.be.server.domain.memberPoint.dto.ChargePointResponse;
import kr.hhplus.be.server.domain.memberPoint.dto.MemberPointResponse;
import kr.hhplus.be.server.domain.product.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FakeStore {

    public ChargePointResponse chargePoint() {
        return new ChargePointResponse(1L, BigDecimal.valueOf(20000), BigDecimal.valueOf(5000));
    }

    public MemberPointResponse memberPoint() {
        return new MemberPointResponse(1L, BigDecimal.valueOf(20000));
    }

    public ProductResponse product() {
        return new ProductResponse(1L, "상의", BigDecimal.valueOf(10000), 200);
    }
}
