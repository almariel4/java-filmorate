package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public void addToFriends(Long userId, Long friendId) {
        if (!userDbStorage.checkIfExistsUser(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        if (!userDbStorage.checkIfExistsUser(friendId)) {
            throw new UserNotFoundException("Пользователь " + friendId + " не существует");
        }
        userDbStorage.addToFriends(userId, friendId);
    }

    public void removeFromFriends(Long userId, Long friendId) {
        userDbStorage.removeFromFriends(userId, friendId);
    }

    public List<User> getAllFriends(Long userId) {
        List<User> usersFriends = userDbStorage.getAllFriends(userId);
        return usersFriends;
    }

    public List<User> getAllCommonFriends(Long userId, Long otherId) {
        if (!userDbStorage.checkIfExistsUser(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        if (!userDbStorage.checkIfExistsUser(userId)) {
            throw new UserNotFoundException("Пользователь " + otherId + " не существует");
        }
//        User user = userDbStorage.getUsers().get(userId);
        List<User> userFriends = userDbStorage.getAllFriends(userId);

//        User other = userDbStorage.getUsers().get(otherId);
        List<User> otherFriends = userDbStorage.getAllFriends(otherId);

        List<User> commonFriends = new ArrayList<>();
        for (User user1 : userFriends) {
            for (User user2 : otherFriends) {
                if (user1.getId().equals(user2.getId())) {
                    commonFriends.add(userDbStorage.getUsers().get(user1.getId()));
                }
            }
        }
        return commonFriends;
    }
}
