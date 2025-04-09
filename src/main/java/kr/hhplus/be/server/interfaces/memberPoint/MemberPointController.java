package kr.hhplus.be.server.interfaces.memberPoint;

import kr.hhplus.be.server.application.memberPoint.MemberPointFacade;
import kr.hhplus.be.server.application.memberPoint.MemberPointResult;
import kr.hhplus.be.server.domain.memberPoint.MemberPointInfo;
import kr.hhplus.be.server.domain.memberPoint.MemberPointService;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/member-points")
public class MemberPointController implements MemberPointApi {

    private final MemberPointFacade facade;
    private final MemberPointService service;

    @Override
    @PatchMapping("{id}/charge")
    public ResponseEntity<ApiResult<MemberPointResponse.ChargeBalance>> charge(
            @PathVariable("id") Long memberId,
            @RequestBody MemberPointRequest.Charge request
    ) {

        MemberPointResult.ChargeBalance result = facade.charge(request.toCriteria(memberId));
        var data = MemberPointResponse.ChargeBalance.of(result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.CHARGE, data));
    }

    @Override
    @GetMapping("{id}")
    public ResponseEntity<ApiResult<MemberPointResponse.Balance>> checkBalance(
            @PathVariable("id") Long memberId
    ) {
        MemberPointInfo.Balance result = service.getBalance(memberId);
        var data = MemberPointResponse.Balance.of(result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.BALANCE_CHECK, data));
    }
}

