package com.nilsson.repo;

import com.nilsson.entity.Member;
import com.nilsson.exception.DatabaseOperationException;
import com.nilsson.util.LanguageManager;
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
        Transaction tx = null;
        try (Session session = this.sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(member);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.MemberDatabaseOperationException"), e);
        }
    }

        @Override
        public Member getMember (Long id){
            try (Session session = this.sessionFactory.openSession()) {
                return session.get(Member.class, id);
            }
        }

        @Override
        public List<Member> getAllMembers () {
            try (Session session = this.sessionFactory.openSession()) {
                // HQL Query
                return session.createQuery("FROM Member", Member.class).list();
            }
        }

        @Override
        public void updateMember (Member member) {
            Transaction tx = null;
            try (Session session = this.sessionFactory.openSession()) {
                tx = session.beginTransaction();
                session.merge(member); // merge updates existing
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.MemberDatabaseOperationException"), e);
            }
        }

            @Override
            public void deleteMember (Member member) {
                Transaction tx = null;
                try (Session session = this.sessionFactory.openSession()) {
                    tx = session.beginTransaction();
                    session.remove(member);
                    tx.commit();
                } catch (Exception e) {
                    if (tx != null) tx.rollback();
                    throw new DatabaseOperationException(LanguageManager.getInstance().getString("error.MemberDatabaseRemoveException"), e);
                }
            }
        }