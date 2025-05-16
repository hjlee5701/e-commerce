package kr.hhplus.be.server.support.lock;

import kr.hhplus.be.server.shared.code.LockErrorCode;
import kr.hhplus.be.server.shared.exception.ECommerceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PubSubLockStrategy implements LockStrategy {

    private final RedissonClient redissonClient;

    @Override
    public LockType getLockType() {
        return LockType.PUB_SUB;
    }

    @Override
    public Object executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, ProceedingJoinPoint joinPoint) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 1. 락 획득
            boolean acquired = lock.tryLock(
                    waitTime,
                    leaseTime,
                    timeUnit
            );

            // 획득 실패
            if (!acquired) {
                throw new ECommerceException(LockErrorCode.ALREADY_LOCKED, lockKey);
            }

            // 2. 실행
            log.info("Lock acquired: {}", lockKey);
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;

        } catch (Throwable e) {
            throw new RuntimeException(e);

        } finally {
            // 3. 락 해제
            if (lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                    log.info("Unlock: {}", lockKey);
                } catch (Exception e) {
                    log.warn("락 해제 중 예외 발생: key={}, message={}", lockKey, e.getMessage());
                }
            } else {
                log.warn("락 해제 실패: 현재 스레드가 락을 보유하지 않음. key={}", lockKey);
            }
        }
    }


    /**
     * 락 획득
     */
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

}
