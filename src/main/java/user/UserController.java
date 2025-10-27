package user;

import io.javalin.http.Handler;

import java.util.Objects;
import java.util.Optional;

public class UserController {

    public static Handler fetchAllUsernames = ctx -> {
        UserDAO dao = new UserDAO();
        Iterable<String> allUsers = dao.getAllUsernames();
        ctx.json(allUsers);
    };

    public static Handler fetchById = ctx -> {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("id")));
        UserDAO dao = new UserDAO();
        Optional<User> user = dao.getUserById(id);
        if (user.isPresent()) {
            ctx.json(user);
        } else {
            ctx.html("Not Found");
        }
    };
}