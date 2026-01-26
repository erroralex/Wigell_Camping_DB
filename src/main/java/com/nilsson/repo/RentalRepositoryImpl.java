package com.nilsson.repo;

import com.nilsson.entity.Rental;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class RentalRepositoryImpl implements RentalRepository {
    private final SessionFactory sessionFactory;

    public RentalRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Rental rental) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(rental);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Rental rental) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(rental); // merge updates existing
            tx.commit();
        }
    }

    @Override
    public List<Rental> getRentalsByMemberId(Long memberId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Rental r JOIN FETCH r.member WHERE r.member.id = :memberId", Rental.class)
                    .setParameter("memberId", memberId)
                    .list();
        }
    }

    @Override
    public List<Rental> getAllRentals() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Rental r JOIN FETCH r.member", Rental.class).list();
        }
    }
}
