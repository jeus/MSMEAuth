/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msme.ir.authmsme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import msme.ir.authmsme.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

/**
 *
 * @author jeus
 */
public class Quickstart {

    private static final transient Logger log = LoggerFactory.getLogger(Quickstart.class);
    //GSON
    static Gson gson;
    static final GsonBuilder builder = new GsonBuilder();

    public static void main(String[] args) {
        builder.excludeFieldsWithoutExposeAnnotation();
        gson = builder.create();
        //have to disable when upload. very important. 
        enableDebugScreen();
        get("/", (req, res) -> {
            return "Spark server with Apache Shiro authentication is running!";
        });
        UserAPI();
    }

    private static void UserAPI() {
        //configure user management
        //Login
        post("/login/", (req, res) -> {
            UserController userCtrl = new UserController();
            String token = userCtrl.authenticate(req.queryParams("user"),req.queryParams("password"));
            if (token.length() > 0) {
                return token;
            }
            res.status(401);
            return "Not able to login!";
        });
    }

    static void init(int port) {
        port(port);

        int maxThreads = 4;
        int minThreads = 2;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);
    }
}
