package com.nilsson.repo;

import com.nilsson.entity.DailyProfit;
import com.nilsson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class DailyProfitRepositoryImpl implements DailyProfitRepository {

    @Override
    public void save(DailyProfit profit) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(profit);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void saveAll(List<DailyProfit> profits) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (DailyProfit p : profits) {
                session.merge(p);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<DailyProfit> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DailyProfit", DailyProfit.class).list();
        }
    }

    @Override
    public DailyProfit findByDate(LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DailyProfit> query = session.createQuery("from DailyProfit where date = :date", DailyProfit.class);
            query.setParameter("date", date);
            return query.uniqueResult();
        }
    }

    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("delete from DailyProfit").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
