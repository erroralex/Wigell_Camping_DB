package com.nilsson.repo;

import com.nilsson.entity.Tent;
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
        try (Session session = sessionFactory.openSession()) {
            return session.get(Tent.class, id);
        }
    }

    @Override
    public List<Tent> getAllTents() {
        try (Session session = sessionFactory.openSession()) {
            // HQL Query
            return session.createQuery("FROM Tent", Tent.class).list();
        }
    }

    @Override
    public void addTent(Tent tent) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(tent); // persist to save
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTent(Tent tent) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(tent); // merge updates existing
            tx.commit();
        }
    }

    @Override
    public void deleteTent(Tent tent) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(tent);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
