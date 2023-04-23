package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final UserService userService;

    @Test
    void createUser() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());
        Optional<User> testUser = Optional.of(userDbStorage.createUser(user));

        assertThat(testUser)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "user@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "almariel")
                                .hasFieldOrPropertyWithValue("name", "Anna")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 3, 9))
                );
    }

    @Test
    void updateUser() {
        User user = new User(1L, "kristina@mail.ru", "kristina21", "Kristina", LocalDate.of(2021, 7, 23), new HashMap<>());
        userDbStorage.createUser(user);
        user.setEmail("polina17@mail.ru");
        user.setLogin("Polina17");
        user.setName("Polina");
        Optional<User> testUser = Optional.of(userDbStorage.updateUser(user));

        assertThat(testUser)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("email", "polina17@mail.ru"))
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("login", "Polina17"))
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("name", "Polina"))
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2021, 7, 23))
                );
    }

    @Test
    void getAllUsers() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());
        User user2 = new User(2L, "kristina@mail.ru", "kristina21", "Kristina", LocalDate.of(2021, 7, 23), new HashMap<>());

        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);

        List<User> users = userDbStorage.getAllUsers();

        assertThat(users)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void getUsers() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());
        User user2 = new User(2L, "kristina@mail.ru", "kristina21", "Kristina", LocalDate.of(2021, 7, 23), new HashMap<>());

        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);


        Optional<User> userOptional1 = Optional.ofNullable(userDbStorage.getUser(1L));
        Optional<User> userOptional2 = Optional.ofNullable(userDbStorage.getUser(2L));

        assertThat(userOptional1)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L)
                );

        assertThat(userOptional2)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    void getUser() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());
        userDbStorage.createUser(user);

        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUser(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void addToFriends() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());
        User friend = new User(0L, "kristina@mail.ru", "kristina21", "Kristina", LocalDate.of(2021, 7, 23), new HashMap<>());
        user = userDbStorage.createUser(user);
        friend = userDbStorage.createUser(friend);
        userService.addToFriends(user.getId(), friend.getId());

        assertThat(userService.getAllFriends(user.getId()))
                .hasSize(1)
                .contains(friend);
    }

    @Test
    void removeFromFriends() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());
        User friend = new User(0L, "kristina@mail.ru", "kristina21", "Kristina", LocalDate.of(2021, 7, 23),  new HashMap<>());
        userDbStorage.createUser(user);
        userDbStorage.createUser(friend);
        userDbStorage.addToFriends(user.getId(), friend.getId());
        userService.removeFromFriends(user.getId(), friend.getId());
        assertThat(user.getFriends())
                .hasSize(0);
    }
}