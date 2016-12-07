/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import msme.ir.authmsme.dao.RoleDao;
import msme.ir.authmsme.dao.UserDao;
import msme.ir.authmsme.entity.ShiroRole;
import msme.ir.authmsme.entity.ShiroUser;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jeus
 */
public class ShiroUserTest {

    public ShiroUserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
       UserDao dao = new UserDao();
       dao.removeUser("InsertDeleteTest");

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void UserInsert() {

        ShiroUser users = new ShiroUser();
        users.setUserName("InsertDeleteTest");
        users.setPersonalNumber("InsertDeleteTest");
        users.setPassword("A123456B1");
        users.setSalt("1234563");
        users.setActive(true);
        UserDao dao = new UserDao();
        dao.insert(users);

        ShiroUser expectedUser = dao.getUser("InsertDeleteTest");

        assertEquals(users.getSalt(), expectedUser.getSalt());
        assertEquals(users.getPersonalNumber(), expectedUser.getPersonalNumber());
        assertEquals(users.getUserName(), expectedUser.getUserName());
        PasswordService passwordService = new DefaultPasswordService();
        assertTrue(passwordService.passwordsMatch(users.getPassword(), expectedUser.getPassword()));
        assertFalse(passwordService.passwordsMatch(users.getPassword(), passwordService.encryptPassword("a123456B")));
    }

//    @Test
    public void UserCall() {
        UserDao dao = new UserDao();
        ShiroUser users = dao.getUser("jeus");
        
        RoleDao rDao = new RoleDao();
        List<ShiroRole> roles  = rDao.getRoles(users.getUserName());
        assertEquals(roles.get(0).getName(), "Admin");
        assertEquals(roles.get(0).getDescription(), "مدیریت اصلی");
    }

    @Test
    public void UserRole() {
    }

    @Test
    public void UserPermission() {
    }

// TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
