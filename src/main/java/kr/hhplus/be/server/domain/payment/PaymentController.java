package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.dto.OrderPaymentResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import kr.hhplus.be.server.global.response.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PaymentController implements PaymentApi {

    private final FakeStore fakeStore;

    @Override
    @PostMapping("/orders/{id}/payments")
    public ResponseEntity<ApiResult<OrderPaymentResponse>> orderPayment (
            @PathVariable("id") Long orderId
    ) {
        OrderPaymentResponse response = fakeStore.orderPayment();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ORDER_PAYMENT, response));
    }
}
