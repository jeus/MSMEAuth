/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msme.ir.authmsme.dao;

import java.util.ArrayList;
import java.util.List;
import msme.ir.authmsme.entity.ShiroPermissions;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author jeus
 */
public class PermissionDao {

    static Session session = null;

    public void insert(ShiroPermissions e) {
        session = TestUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(e);
        tx.commit();
        //session.flush();
        session.close();
    }

    public List<ShiroPermissions> getPermissions(String roleName) {
        Session session = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<ShiroPermissions> permission = new ArrayList<>();
        try {
            tx = session.beginTransaction();
            permission = session.createQuery("FROM ShiroPermissions s WHERE s."
                    + "shiroRoleses = ?").setParameter(0, roleName).list();
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return permission;
    }

    public ShiroPermissions getPermission(String permissName) {
        Session session = TestUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ShiroPermissions permission = null;
        try {
            tx = session.beginTransaction();
            permission = session.createQuery("FROM ShiroPermissions s WHERE s."
                    + "name = ?", ShiroPermissions.class).setParameter(0, permissName).getSingleResult();
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return permission;
    }

}
