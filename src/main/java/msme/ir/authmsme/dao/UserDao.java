package msme.ir.authmsme.dao;

import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import msme.ir.authmsme.entity.ShiroUsers;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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

            user = session.createQuery(" From shiroUser s where s.userName = ?",
                    ShiroUsers.class).setParameter(0, userName).getSingleResult();
            tx.commit();

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

    public void updatePassword(String userName, String password) {
        Session session = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ShiroUsers shiroUser = null;
            if ((shiroUser = getUser(userName)) != null) {
                shiroUser.setPassword(password);
                session.update(shiroUser);
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
