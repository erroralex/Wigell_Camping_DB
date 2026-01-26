package com.nilsson.util;

import com.nilsson.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import java.util.Properties;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {}

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (getSessionFactory().isOpen()) {
            getSessionFactory().close();
        }
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            props.load(HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.properties"));
            configuration.setProperties(props);

            // Entities
            configuration.addAnnotatedClass(Member.class);
            configuration.addAnnotatedClass(Rental.class);
            configuration.addAnnotatedClass(Vehicle.class);
            configuration.addAnnotatedClass(Gear.class);
            configuration.addAnnotatedClass(Tent.class);
            configuration.addAnnotatedClass(DailyProfit.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}