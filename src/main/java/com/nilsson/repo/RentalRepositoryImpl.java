package com.nilsson.repo;

import com.nilsson.entity.Rental;
import com.nilsson.exception.DatabaseOperationException;
import com.nilsson.util.LanguageManager;
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
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(rental);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DatabaseOperationException(LanguageManager.getInstance().getString(
                    "error.RentalDatabaseOperationException"), e);
        }
    }

    @Override
    public void update(Rental rental) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(rental);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.RentalDatabaseOperationException"), e);
        }
    }

    @Override
    public List<Rental> getRentalsByMemberId(Long memberId) {
        try (Session session = this.sessionFactory.openSession()) {
            return session.createQuery("FROM Rental r JOIN FETCH r.member WHERE r.member.id = :memberId", Rental.class)
                    .setParameter("memberId", memberId)
                    .list();
        }
    }

    @Override
    public List<Rental> getAllRentals() {
        try (Session session = this.sessionFactory.openSession()) {
            return session.createQuery("FROM Rental r JOIN FETCH r.member", Rental.class).list();
        }
    }
}
