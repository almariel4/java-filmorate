package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Getter
//    static добавлен для тестов для обнуления поля @BeforeEach
    private static final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(users.size() + 1);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь id='{}', name='{}'", user.getId(), user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new RuntimeException("Пользователя с id=" + user.getId() + " отсутствует в списке пользователей");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.info("Изменен пользователь id='{}', name='{}'", user.getId(), user.getName());
        return user;
    }
}
