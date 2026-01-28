package com.nilsson.repo;

import com.nilsson.entity.Vehicle;
import com.nilsson.exception.DatabaseOperationException;
import com.nilsson.util.LanguageManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class VehicleRepositoryImpl implements VehicleRepository {
    private final SessionFactory sessionFactory;

    public VehicleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Vehicle getVehicle(Long id) {
        try (Session session = this.sessionFactory.openSession()) {
            return session.get(Vehicle.class, id);
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        try (Session session = this.sessionFactory.openSession()) {
            return session.createQuery("FROM Vehicle", Vehicle.class).list();
        }
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(vehicle); // persist to save
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback(); // Always rollback on error
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.VehicleDatabaseOperationException"), e);
        }
    }

    @Override
    public void updateVehicle(Vehicle vehicle) {
        try (Session session = this.sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(vehicle); // merge updates existing
            tx.commit();
        }
    }
    @Override
    public void deleteVehicle(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(vehicle);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback(); // Always rollback on error
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.VehicleDatabaseRemoveException"), e);
        }
    }

    @Override
    public List<Vehicle> findByIsRentedFalse() {
        try (Session session = this.sessionFactory.openSession()) {
            return session.createQuery("FROM Vehicle v WHERE v.isRented = false", Vehicle.class).list();
        }
    }
}
