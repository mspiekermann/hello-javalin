# Minimal REST Service with Javalin


## Introduction

Setting up microservices that provide REST endpoints to handle data objects is a repetitive tasks. In the Java ecosystem, there are prominent frameworks that can be used to achieve such services like [Spring Boot](https://spring.io/projects/spring-boot). However, for smaller webservices, they come with a lot overhead.

While looking for alternatives, I stumbled over [Javalin](https://javalin.io/), which is a lightweight web framework written for Java and Kotlin. It’s written on top of the Jetty web server, which makes it highly performant.

In the getting started process and get first insights I was not able to find an up-to-date tutorial, which shows application of Javalin beside the (great) project [documentation](https://javalin.io/documentation) on the project website.

Thus, I will share this repo, which will set up a minimal example of a webservice using `Javalin v6.7`, handling (fake and hardcoded) user information.

>[Baeldung](https://www.baeldung.com/javalin-rest-microservices) provides a nice tutorial, which is not aligned with newer Javalin versions (e.g. it uses :id parameters in their routes, which was deprecated and changed to {id}). However, as the storyline remains I used this as basis.

## Dependencies

To create our basic webservice, we need to add some dependencies. Obviously, we need to add Javalin itself. In addition, we **may** add the [slf4j](https://slf4j.org/) dependency to avoid a warning in console output, while starting the application.

```bash
implementation("io.javalin:javalin:6.7.0")
implementation("org.slf4j:slf4j-simple:2.0.16")
```

## Setting up Javalin Application

Javalin makes setting up a basic application easy. We’re going to start by defining our main class and setting up a simple “Hello World” application. For this, within the main method we add the following set up:

```java
 var app = Javalin.create()
        .get("/", ctx -> ctx.result("Hello World"))
        .start(7070);
```

This creates a new instance of Javalin, listening on port 7070, and add a first endpoint listening for GET requests at the root (/) endpoint.

```bash
./gradlew build
./gradlew run
```
Run Gradle commands to build and starts our application and visit `http://localhost:7070` to see the result.

## Adding the User Handling Use Case

After having the basic *Hello World* running, we put in some more "realistic" use case for Javalin application: Handling user data.

First, we need to create the User model, we are working with. Therefore, we create a Record User with just two attributes (*id* and *name*).

Second, we set up our data access objects (DAO). We define a two methods `getUserById(int id)` and `getAllUsernames()` that we can use to check webservice's features.

> For the sake of simplicity, a simple in-memory list of fake users will be used. Further, add/update/delete features not considered yet.

```java
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
```

Finally, we add a controller to our application and add the corresponding Javalin Handlers:

```java
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
```
For the `fetchById` method, the Optional<User> resulting from our UserDAO request is just forwarded. This is probably nothing you would do often. Whatever, I just did... But, this led me to fall into a "trap". Javalin uses [Jackson](https://github.com/FasterXML/jackson) for JSON parsing, which requires the corresponding `jackson-datatype-jdk8` module to correctly map Optionals.

To fix this and use the Optional directly, two additions have to be made:

1. add this to your dependencies list in `build.gradle.kts`
2. 
```java
 implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.17.2")
```

1. Configure (update) the internal ObjectMapper used by the Javalin to register the module

```java
var app = Javalin.create(cfg -> cfg.jsonMapper(new JavalinJackson().
                updateMapper(mapper -> mapper.registerModule(new Jdk8Module())))
        )
        .get("/", ctx -> ctx.result("Hello World"))
        .start(7070);
```

## Adding the Routes

With the two ways of fetching user data in place, we need the corresponding routes (REST endpoints) to expose them. For this reason, we register two additional routes in our main application:

```java
app.get("/users", UserController.fetchAllUsernames);
app.get("/users/{id}", UserController.fetchById);
```

After going through the gradle build and run tasks again, we have both endpoints available and can send requests. Thereby, calling http://localhost:7000/users will list all users, while calling http://localhost:7000/users/3 will get the single User JSON object with the id 3. Change the id for retrieving different user data (0-4 available in this example).

## Extending Routes

This simple example uses shortcuts like the in-memory `Map` for user data, directly transfers the Optional to response, etc. This is fine as a starting point and show how to set up a quick running example with Javalin. However, while retrieving data (via *GET*) is a vital task of most microservices, *POST*, *DELETE*, and *PUT* are too. Javalin ofc provides the full set of handlers that are required for such tasks.

So for now, please have a look at the Javalin documentation on [Handlers](https://javalin.io/documentation#handlers) for more information.

## Conclusion

With Javalin it is pretty easy to set up microservices without the overhead of other frameworks. In above example a walkthrough of a simple application to retrieve data was done.

For more advanced examples of how to use Javalin, be sure to check out the [documentation](https://javalin.io/documentation).