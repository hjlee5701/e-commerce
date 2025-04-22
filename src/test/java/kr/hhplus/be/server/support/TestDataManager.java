package kr.hhplus.be.server.support;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class TestDataManager {
    private final EntityManager entityManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public <T> void persist(List<T> entities) {
        for (T entity : entities) {
            entityManager.persist(entity);
        }
    }

    @Transactional
    public <T> void persist(T entity) {
        entityManager.persist(entity);
    }

    @Transactional
    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    public void cleanupAll() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        try {
            for (String entityName : getAllTableNames()) {
                jdbcTemplate.execute("TRUNCATE TABLE " + entityName);
            }
        } finally {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    private Set<String> getAllTableNames() {
        return Set.of(
                "member", "member_point", "member_point_history",
                "coupon", "coupon_item",
                "product", "product_stock",
                "orders", "order_item", "payment"
        );
    }

}
