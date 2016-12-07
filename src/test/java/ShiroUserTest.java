

import java.util.ArrayList;
import java.util.List;
import msme.ir.authmsme.dao.PermissionDao;
import msme.ir.authmsme.dao.RoleDao;
import msme.ir.authmsme.dao.UserDao;
import msme.ir.authmsme.entity.ShiroPermission;
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

    List<ShiroRole> roles = new ArrayList<>();

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

    @Test
    public void UserRole() {
        UserDao dao = new UserDao();
        ShiroUser users = dao.getUser("jeus");

        RoleDao rDao = new RoleDao();
        roles = rDao.getRoles(users.getUserName());

        assertEquals(roles.get(0).getName(), "Admin");
        assertEquals(roles.get(0).getDescription(), "مدیریت اصلی");
    }

    @Test
    public void UserPermission() {
        UserDao dao = new UserDao();
        ShiroUser users = dao.getUser("jeus");
        
        PermissionDao pDao = new PermissionDao();
        List<ShiroPermission> permissions = pDao.getPermissions("Admin");
        assertEquals(permissions.get(0).getName(), "panel1");

    }

}
