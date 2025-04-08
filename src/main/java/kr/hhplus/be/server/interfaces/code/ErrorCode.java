package kr.hhplus.be.server.interfaces.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode extends Code {
    HttpStatus getHttpStatus();
}
