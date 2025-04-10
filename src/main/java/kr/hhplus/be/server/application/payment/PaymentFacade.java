package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentFacade {

    private final OrderService orderService;

    public void createPayment(PaymentCriteria.Create criteria) {
        // 주문 조회
        // 결제 처리 > 쿠폰 검사/적용/상태 변경 (성공/실패) > 금액 차감 (쿠폰 있을 경우) > 결제 (성공/실패)
        // 분기) 재고 차감 롤백
        // 주문 정보 업데이트 (총가격 변동)

    }
}
