package kr.hhplus.be.server.support.instancio;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BatchDataInserter {

    private final EntityManager entityManager;

    public <T> void insertEntities(List<T> entities) {
        Session session = entityManager.unwrap(Session.class);
        StatelessSession statelessSession = session.getSessionFactory().openStatelessSession();
        Transaction transaction = statelessSession.beginTransaction();

        try {
            for (int i = 0; i < entities.size(); i++) {
                statelessSession.insert(entities.get(i));

                if (i % 1000 == 0 && i > 0) {
                    transaction.commit();
                    transaction = statelessSession.beginTransaction();
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            statelessSession.close();
        }
    }
}
