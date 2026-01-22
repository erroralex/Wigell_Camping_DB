package com.nilsson.repo;

import com.nilsson.entity.Vehicle;
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
        try (Session session = sessionFactory.openSession()) {
            return session.get(Vehicle.class, id);
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        try (Session session = sessionFactory.openSession()) {
            // HQL Query
            return session.createQuery("FROM Vehicle", Vehicle.class).list();
        }
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(vehicle); // persist to save
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVehicle(Vehicle vehicle) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(vehicle); // merge updates existing
            tx.commit();
        }
    }
    @Override
    public void deleteVehicle(Vehicle vehicle) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(vehicle);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
