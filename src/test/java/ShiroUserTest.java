/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import msme.ir.authmsme.dao.UserDao;
import msme.ir.authmsme.entity.ShiroUsers;
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
        dao.removeUser("jeus");

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

        ShiroUsers users = new ShiroUsers();
        users.setUserName("jeus");
        users.setPersonalNumber("jeus");
        users.setPassword("A123456B");
        users.setSalt("123456");
        users.setActive(true);
        UserDao dao = new UserDao();
        dao.insert(users);

        ShiroUsers expectedUser = dao.getUser("jeus");

        assertEquals(users.getSalt(), expectedUser.getSalt());
        assertEquals(users.getPersonalNumber(), expectedUser.getPersonalNumber());
        assertEquals(users.getUserName(), expectedUser.getUserName());
        PasswordService passwordService = new DefaultPasswordService();
        assertTrue(passwordService.passwordsMatch(users.getPassword(), expectedUser.getPassword()));
        assertFalse(passwordService.passwordsMatch(users.getPassword(), passwordService.encryptPassword("a123456B")));
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
