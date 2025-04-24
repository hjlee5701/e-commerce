package kr.hhplus.be.server.shared.logging;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Trace ID 설정
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        log.info("[Filter] Request: {} {} from {}", req.getMethod(), req.getRequestURI(), req.getRemoteAddr());


        try {
            chain.doFilter(request, response);
        } finally {
            log.info("[Filter] Response: {} {} -> status {}", req.getMethod(), req.getRequestURI(), res.getStatus());
            MDC.clear(); // 중요: 메모리 누수 방지
        }
    }
}
