package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    @Getter
    private final Map<Integer, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(int userId) {
//            FIXME: тесты Postman выдают ошибку 500, хотя хендлер обрабатывает верно
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователь с " + userId + "не найден");
        }
        return users.get(userId);
    }

    public User createUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(users.size() + 1);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь id='{}', name='{}'", user.getId(), user.getName());
        return user;
    }

    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователя с id=" + user.getId() + " отсутствует в списке пользователей");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.info("Изменен пользователь id='{}', name='{}'", user.getId(), user.getName());
        return user;
    }
}
