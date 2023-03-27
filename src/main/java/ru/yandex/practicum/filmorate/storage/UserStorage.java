package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Map<Long, User> getUsers();

    List<User> getAllUsers();

    User getUser(Long id);

}
