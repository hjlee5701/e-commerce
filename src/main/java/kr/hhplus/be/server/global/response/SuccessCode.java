package kr.hhplus.be.server.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements Code {
    ;
    private final String code;
    private final String message;

}
