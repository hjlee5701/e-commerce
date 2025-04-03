package kr.hhplus.be.server.util;

import kr.hhplus.be.server.domain.memberPoint.dto.ChargePointResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FakeStore {

    public ChargePointResponse chargePoint() {
        return new ChargePointResponse(1L, BigDecimal.valueOf(20000), BigDecimal.valueOf(5000));
    }
}
