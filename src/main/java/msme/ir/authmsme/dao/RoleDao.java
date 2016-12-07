/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msme.ir.authmsme.dao;

import java.util.ArrayList;
import java.util.List;
import msme.ir.authmsme.entity.ShiroPermission;
import msme.ir.authmsme.entity.ShiroRole;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author jeus
 */
public class RoleDao {

    static Session session = null;

    public void insert(ShiroRole e) {
        session = TestUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(e);
        tx.commit();
        //session.flush();
        session.close();
    }

    public List<ShiroRole> getRoles(String userName) {
        Session sessionLocal;
        sessionLocal = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<ShiroRole> roles = new ArrayList<>();
        try {
            tx = sessionLocal.beginTransaction();
            roles = sessionLocal.createQuery("SELECT sr FROM ShiroUser su join "
                    + "su.shiroRoles sr WHERE su.userName=?",ShiroRole.class)
                    .setParameter(0, userName).getResultList();
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
