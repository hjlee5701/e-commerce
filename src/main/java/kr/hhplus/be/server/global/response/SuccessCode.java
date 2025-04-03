package kr.hhplus.be.server.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements Code {
    CHARGE("200", "충전에 성공했습니다."),
    BALANCE_CHECK("200","보유 금액 조회 성공했습니다."),
    ;
    private final String code;
    private final String message;

}
