package kr.hhplus.be.server.support.lock;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.TimeUnit;

public interface LockStrategy {
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);
    LockType getLockType();
    Object executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, ProceedingJoinPoint joinPoint);

}

