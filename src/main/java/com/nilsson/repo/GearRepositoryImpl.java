package com.nilsson.repo;

import com.nilsson.entity.Gear;
import com.nilsson.exception.DatabaseOperationException;
import com.nilsson.util.LanguageManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class GearRepositoryImpl implements GearRepository {
    private final SessionFactory sessionFactory;

    public GearRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Gear getGear(Long id) {
        try (Session session = this.sessionFactory.openSession()) {
            return session.get(Gear.class, id);
        }
    }

    @Override
    public List<Gear> getAllGear() {
        try (Session session = this.sessionFactory.openSession()) {
            return session.createQuery("FROM Gear", Gear.class).list();
        }
    }

    @Override
    public void save(Gear gear) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(gear);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.GearDatabaseOperationException"), e);
        }
    }

    @Override
    public void update(Gear gear) {
        try (Session session = this.sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(gear);
            tx.commit();
        }
    }

    @Override
    public void delete(Gear gear) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(gear);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.GearDatabaseRemoveException"), e);
        }
    }
}
