/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msme.ir.authmsme.dao;

import java.util.ArrayList;
import java.util.List;
import msme.ir.authmsme.entity.ShiroPermissions;
import msme.ir.authmsme.entity.ShiroRoles;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author jeus
 */
public class RoleDao {

    static Session session = null;

    public void insert(ShiroRoles e) {
        session = TestUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(e);
        tx.commit();
        //session.flush();
        session.close();
    }

    public List<ShiroRoles> getRoles(String userName) {
        Session sessionLocal;
        sessionLocal = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<ShiroRoles> roles = new ArrayList<>();
        try {
            tx = sessionLocal.beginTransaction();
            roles = sessionLocal.createQuery("FROM ShiroRoles r Where r.userName"
                    + " = ?",ShiroRoles.class).setParameter(0, userName).getResultList();
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            sessionLocal.close();
        }
        return roles;
    }

}
