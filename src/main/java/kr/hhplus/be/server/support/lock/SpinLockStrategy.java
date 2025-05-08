package kr.hhplus.be.server.support.lock;

import kr.hhplus.be.server.shared.code.LockErrorCode;
import kr.hhplus.be.server.shared.exception.ECommerceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class SpinLockStrategy implements LockStrategy {

    private final RedisTemplate<String, String> redisTemplate;
    private final ThreadLocal<String> lockValueHolder = new ThreadLocal<>();
    private final static String SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>();
    static {
        UNLOCK_SCRIPT.setScriptText(SCRIPT);
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    /**
     * 락 획득
     */
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        String lockValue = UUID.randomUUID().toString();
        long end = System.currentTimeMillis() + waitTime;

        while (System.currentTimeMillis() < end) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, leaseTime, unit);
            if (Boolean.TRUE.equals(success)) {
                lockValueHolder.set(lockValue);
                return true;
            }

            try {
                Thread.sleep(100); // 짧은 시간 대기 후 재시도 (Fixed delay)
                log.info("Retry Lock : {} / {}", lockValue, lockKey);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    @Override
    public void unlock(String lockKey) {
        String lockValue = lockValueHolder.get();
        if (lockValue == null) {
            throw new ECommerceException(LockErrorCode.UNHOLD_LOCK, lockKey);
        }

        Long result = redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
        if (result == 0L) {
            log.warn("락 해제 실패: 키가 없거나 값이 일치하지 않음. key={}, value={}", lockKey, lockValue);
        }

        lockValueHolder.remove(); // ThreadLocal 정리
    }
}
