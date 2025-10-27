import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.json.JavalinJacksonKt;
import user.UserController;

public class JavalinApp {

    public static void main(String[] args) {

        JavalinJackson.defaultMapper().registerModule(new Jdk8Module()); // <-- handles java.util.Optional

        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);

        app.get("/users", UserController.fetchAllUsernames);
        app.get("/users/{id}", UserController.fetchById);
    }


}

