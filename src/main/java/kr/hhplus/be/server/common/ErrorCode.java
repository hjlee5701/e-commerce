package kr.hhplus.be.server.interfaces.common;

import org.springframework.http.HttpStatus;

public interface ErrorCode extends Code {
    HttpStatus getHttpStatus();
}
