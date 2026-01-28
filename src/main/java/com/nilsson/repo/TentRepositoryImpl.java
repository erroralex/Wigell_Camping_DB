package com.nilsson.repo;

import com.nilsson.entity.Tent;
import com.nilsson.exception.DatabaseOperationException;
import com.nilsson.util.LanguageManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TentRepositoryImpl implements TentRepository {
    private final SessionFactory sessionFactory;

    public TentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Tent getTent(Long id) {
        try (Session session = this.sessionFactory.openSession()) {
            return session.get(Tent.class, id);
        }
    }

    @Override
    public List<Tent> getAllTents() {
        try (Session session = this.sessionFactory.openSession()) {
            return session.createQuery("FROM Tent", Tent.class).list();
        }
    }

    @Override
    public void save(Tent tent) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(tent);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.TentDatabaseOperationException"), e);
        }
    }

    @Override
    public void update(Tent tent) {
        try (Session session = this.sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(tent);
            tx.commit();
        }
    }

    @Override
    public void delete(Tent tent) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(tent);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.TentDatabaseRemoveException"), e);
        }
    }
}
