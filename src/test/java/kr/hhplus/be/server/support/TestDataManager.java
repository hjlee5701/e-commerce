package kr.hhplus.be.server.support;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TestDataManager {
    private final EntityManager entityManager;

    @Transactional
    public <T> void persist(List<T> entities) {
        for (T entity : entities) {
            entityManager.persist(entity);
        }
    }
    public <T> void persist(T entity) {
        entityManager.persist(entity);
    }
    @Transactional
    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
