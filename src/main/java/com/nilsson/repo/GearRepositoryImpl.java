package com.nilsson.repo;

import com.nilsson.entity.Gear;
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
        try (Session session = sessionFactory.openSession()) {
            return session.get(Gear.class, id);
        }
    }

    @Override
    public List<Gear> getAllGear() {
        try (Session session = sessionFactory.openSession()) {
            // HQL Query
            return session.createQuery("FROM Gear", Gear.class).list();
        }
    }

    @Override
    public void addGear(Gear gear) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(gear); // persist to save
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGear(Gear gear) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(gear); // merge updates existing
            tx.commit();
        }
    }

    @Override
    public void deleteGear(Gear gear) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(gear);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Gear> findByIsRentedFalse() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Gear g WHERE g.isRented = false", Gear.class).list();
        }
    }
}
