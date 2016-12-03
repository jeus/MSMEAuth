package msme.ir.authmsme.dao;

import msme.ir.authmsme.entity.ShiroUsers;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDao {

    static Session session = null;

    public void insert(ShiroUsers e) {
        session = TestUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(e);
        tx.commit();
        //session.flush();
        session.close();
    }

    public ShiroUsers getUser(String userName) {
        Session session = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ShiroUsers user = null;
        try {
            tx = session.beginTransaction();
            user = (ShiroUsers) session.createQuery("FROM ShiroUsers").uniqueResult();
            if (user != null) {
                System.out.print("personalNumber: " + user.getPersonalNumber());
                System.out.print("getLastName" + user.getUserName());
                System.out.println("salt" + user.getSalt());

                tx.commit();
            }
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }

    public void removeUser(String userName) {
        Session session = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ShiroUsers shiroUsers = null;
            if ((shiroUsers = getUser(userName)) != null) {
                System.out.println("INJA HAM OMAD=====================================");
                session.delete(shiroUsers);
                tx.commit();
            }
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
