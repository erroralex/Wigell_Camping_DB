package com.nilsson.repo;

import com.nilsson.entity.Member;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepository {

    private final SessionFactory sessionFactory;

    public MemberRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(member);
            tx.commit();
        }
    }

    @Override
    public Member getMember(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Member.class, id);
        }
    }

    @Override
    public List<Member> getAllMembers() {
        try (Session session = sessionFactory.openSession()) {
            // HQL Query
            return session.createQuery("FROM Member", Member.class).list();
        }
    }

    @Override
    public void updateMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(member); // merge updates existing
            tx.commit();
        }
    }

    @Override
    public void deleteMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(member);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}