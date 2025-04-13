package kr.hhplus.be.server.common;

public interface TestFixture<T> {
    T create();

    default TestFixture<T> clone(T target) {
        FixtureReflectionUtils.reflect(target, this);
        return this;
    }
}
