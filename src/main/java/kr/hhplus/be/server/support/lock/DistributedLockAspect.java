package kr.hhplus.be.server.support.lock;

import kr.hhplus.be.server.shared.code.LockErrorCode;
import kr.hhplus.be.server.shared.exception.ECommerceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class DistributedLockAspect {

    private final SpinLockStrategy spinLockStrategy;
    private final PubSubLockStrategy pubSubLockStrategy;
    private final LockManager lockManager;

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";


    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable{

        // 1. AOP에서 파라미터 추출
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        // 2. SpEL로 키 생성
        String key = SpelValueResolver.resolve(
                paramNames, args, distributedLock.keyExpression()
        );

        // 3. 키 유효성 검사
        Assert.hasText(key, "분산 락 키(SPEL 평가 결과)가 유효하지 않습니다.");


        // 4. 최종 락 키 조합
        String lockKey = REDISSON_LOCK_PREFIX + key;

        // 5. 분산락 전략 선택
        LockStrategy selectedStrategy = switch (distributedLock.type()) {
            case PUB_SUB -> pubSubLockStrategy;
            case SPIN -> spinLockStrategy;
            default -> throw new IllegalStateException("Unknown LockType: " + distributedLock.type());
        };

        return lockManager.executeWithLock(
                lockKey,
                selectedStrategy,
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit(),
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (RuntimeException | Error e) {
                        throw e;
                    } catch (Throwable t) {
                        throw new ECommerceException(LockErrorCode.AOP_ERROR, t);
                    }
                }
        );

    }
}