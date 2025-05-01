package kr.hhplus.be.server.support.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String keyExpression();

    LockType type();

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    long waitTime() default 5000L;

    long leaseTime() default 3000L;
}

