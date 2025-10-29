import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import user.UserController;

public class JavalinApp {

    public static void main(String[] args) {

        var app = Javalin.create(cfg -> cfg.jsonMapper(new JavalinJackson().updateMapper(mapper -> mapper.registerModule(new Jdk8Module()))))
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);

        app.get("/users", UserController.fetchAllUsernames);
        app.get("/users/{id}", UserController.fetchById);
    }

}

