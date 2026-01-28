package com.nilsson.repo;

import com.nilsson.entity.DailyProfit;
import com.nilsson.exception.DatabaseOperationException;
import com.nilsson.util.HibernateUtil;
import com.nilsson.util.LanguageManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.time.LocalDate;
import java.util.List;

public class ProfitRepositoryImpl implements ProfitRepository {
    private final SessionFactory sessionFactory;

    public ProfitRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(DailyProfit profit) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(profit);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback(); // Always rollback on error
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.ProfitDatabaseOperationException"), e);
        }
    }

    @Override
    public void saveAll(List<DailyProfit> profits) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            for (DailyProfit p : profits) {
                session.merge(p);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback(); // Always rollback on error
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.ProfitDatabaseOperationException"), e);
        }
    }

    @Override
    public List<DailyProfit> findAll() {
        try (Session session = this.sessionFactory.openSession()) {
            return session.createQuery("from DailyProfit", DailyProfit.class).list();
        }
    }

    @Override
    public DailyProfit findByDate(LocalDate date) {
        try (Session session = this.sessionFactory.openSession()) {
            Query<DailyProfit> query = session.createQuery("from DailyProfit where date = :date", DailyProfit.class);
            query.setParameter("date", date);
            return query.uniqueResult();
        }
    }

    @Override
    public void deleteAll() {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.createQuery("delete from DailyProfit").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback(); // Always rollback on error
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.ProfitDatabaseRemoveException"), e);
        }
    }
}
