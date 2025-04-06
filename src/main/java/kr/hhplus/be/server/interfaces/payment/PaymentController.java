package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
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
    public ResponseEntity<ApiResult<PaymentResponse.Created>> orderPayment (
            @PathVariable("id") Long orderId
    ) {
        PaymentResponse.Created response = fakeStore.orderPayment();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ORDER_PAYMENT, response));
    }
}
