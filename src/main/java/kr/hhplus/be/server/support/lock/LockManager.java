package kr.hhplus.be.server.support.lock;

import kr.hhplus.be.server.shared.code.LockErrorCode;
import kr.hhplus.be.server.shared.exception.ECommerceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
@Slf4j
public class LockManager {

    public <T> T executeWithLock(
            String lockKey,
            LockStrategy lockStrategy,
            long waitTime,
            long leaseTime,
            TimeUnit unit,
            Supplier<T> executeFunction
    ) {

        try {
            // 1. 락 획득
            boolean acquired = lockStrategy.tryLock(lockKey, waitTime, leaseTime, unit);

            // 획득 실패
            if (!acquired) {
                throw new ECommerceException(LockErrorCode.ALREADY_LOCKED, lockKey);
            }
            log.info("Acquire Lock, {}", lockKey);
            // 2. 실행
            return executeFunction.get();
        } finally {
            try {
                // 3. 락 해제
//                lockStrategy.unlock(lockKey);
                log.info("Unlock : {}", lockKey);

            } catch (Exception e) {
                log.info("Already Unlock : {}", lockKey);
            }
        }

    }
}
