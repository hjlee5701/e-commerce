package kr.hhplus.be.server.shared.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode extends Code {
    HttpStatus getHttpStatus();
}
