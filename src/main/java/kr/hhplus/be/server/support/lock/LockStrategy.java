package kr.hhplus.be.server.support.lock;

import java.util.concurrent.TimeUnit;

public interface LockStrategy {
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);

    void unlock(String lockKey);
}

