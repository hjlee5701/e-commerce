package kr.hhplus.be.server.interfaces.memberPoint;

import kr.hhplus.be.server.application.memberPoint.MemberPointFacade;
import kr.hhplus.be.server.application.memberPoint.MemberPointResult;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/member-points")
public class MemberPointController implements MemberPointApi {

    private final FakeStore fakeStore;
    private final MemberPointFacade facade;

    @Override
    @PatchMapping("{id}/charge")
    public ResponseEntity<ApiResult<MemberPointResponse.Balance>> charge(
            @PathVariable("id") Long memberId,
            @RequestBody MemberPointRequest.Charge request
    ) {

        MemberPointResult.Balance result = facade.charge(request.toCriteria(memberId));
        var data = MemberPointResponse.Balance.of(result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.CHARGE, data));
    }

    @Override
    @GetMapping("{id}")
    public ResponseEntity<ApiResult<MemberPointResponse.Balance>> checkBalance(
            @PathVariable("id") Long memberId
    ) {
        MemberPointResponse.Balance data = fakeStore.balance();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.BALANCE_CHECK, data));
    }
}

