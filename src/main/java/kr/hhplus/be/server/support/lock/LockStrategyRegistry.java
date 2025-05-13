package kr.hhplus.be.server.support.lock;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LockStrategyRegistry {

    private final Map<LockType, LockStrategy> strategies;

    public LockStrategy getLockStrategy(LockType lockType) {
        return strategies.get(lockType);
    }

    public LockStrategyRegistry(List<LockStrategy> templates) {
        this.strategies = templates.stream()
                .collect(Collectors.toMap(
                        LockStrategy::getLockType,
                        lockType -> lockType
                ));
    }

}
