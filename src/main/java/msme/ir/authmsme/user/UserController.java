package msme.ir.authmsme.user;

import msme.ir.authmsme.dao.UserDao;
import msme.ir.authmsme.entity.ShiroUsers;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;

public class UserController {

    // Authenticate the user by hashing the inputted password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    public static String authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return "";
        }
        Subject subject = SecurityUtils.getSubject();
        subject.getSession(true);

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);

        } catch (UnknownAccountException uae) {

            return "";
        } catch (IncorrectCredentialsException ice) {

            return "";
        } catch (LockedAccountException lae) {

            return "";
        } catch (ExcessiveAttemptsException eae) {

            return "";
        } catch (AuthenticationException ae) {
            //unexpected error?

            return "";
        }
        UserDao userDao = new UserDao();
        ShiroUsers shiroUser = null;

        //Add the user object to the session
        if ((shiroUser = userDao.getUser(username)) == null) {
            return "";
        }
        subject.getSession().setAttribute("user", shiroUser);
        return (String) subject.getSession().getId();
//        PasswordService passwordService = new DefaultPasswordService();
//        char[] chararray = password.toCharArray();
//        return passwordService.passwordsMatch(chararray, shiroUsers.getPassword());
    }

    // This method call updatePassword in Dao and change password after authenticate userName and pass is valid.
    public static void setPassword(String username, String oldPassword, String newPassword) {
        if (!authenticate(username, oldPassword).equals("")) {
            UserDao userDao = new UserDao();
            userDao.updatePassword(username, newPassword);
        }
    }
}
