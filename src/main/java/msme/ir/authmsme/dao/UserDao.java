package msme.ir.authmsme.dao;

import javax.persistence.NoResultException;
import msme.ir.authmsme.entity.ShiroUser;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {

    static Session session = null;
    private static final transient Logger log = LoggerFactory.getLogger(UserDao.class);

    public void insert(ShiroUser e) {
        session = TestUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(e);
        tx.commit();
        //session.flush();
        session.close();
    }

    public ShiroUser getUser(String userName) {
        Session session = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ShiroUser user = null;
        try {
            tx = session.beginTransaction();

            if ((user = session.createQuery("From ShiroUser s where s.userName=?",
                    ShiroUser.class).setParameter(0, userName)
                    .getSingleResult()) != null) {
                tx.commit();
            }
        } catch (NoResultException nre) {
            log.error(nre.getMessage());
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error(e.getMessage());
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
            ShiroUser shiroUsers = null;
            if ((shiroUsers = getUser(userName)) != null) {
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
            ShiroUser shiroUser = null;
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
