package user;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDAO {

    private final List<User> users = new ArrayList<>(Arrays.asList(
            new User(0, "Steve Rogers"),
            new User(1, "Tony Stark"),
            new User(2, "Bruce Banner"),
            new User(3, "Natasha Romanoff"),
            new User(4, "Carol Danvers")
    ));

    Optional<User> getUserById(int id) {
        return users.stream()
                .filter(u -> u.id() == id)
                .findAny();
    }

    Iterable<String> getAllUsernames() {
        return users.stream()
                .map(User::name)
                .collect(Collectors.toList());
    }
}
