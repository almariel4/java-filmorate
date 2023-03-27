package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addToFriends(Long userId, Long friendId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new UserNotFoundException("Пользователь " + friendId + " не существует");
        }
        User user = userStorage.getUsers().get(userId);
        User friend = userStorage.getUsers().get(friendId);
        Set<Long> friends = user.getFriends();
        friends.add(friendId);
        user.setFriends(friends);
        userStorage.getUsers().put(userId, user);

        Set<Long> friends2 = friend.getFriends();
        friends2.add(userId);
        friend.setFriends(friends2);
        userStorage.getUsers().put(friendId, friend);

        return user;
    }

    public User removeFromFriends(Long userId, Long friendId) {
        User user = userStorage.getUsers().get(userId);
        User friend = userStorage.getUsers().get(friendId);
        Set<Long> friends = user.getFriends();
        friends.remove(friendId);
        user.setFriends(friends);
        userStorage.getUsers().remove(userId);
        userStorage.getUsers().put(userId, user);

        Set<Long> friends2 = friend.getFriends();
        friends2.remove(userId);
        friend.setFriends(friends2);
        userStorage.getUsers().remove(friendId);
        userStorage.getUsers().put(friendId, friend);

        return user;
    }

    public List<User> getAllFriends(Long userId) {
        Set<Long> usersIds = userStorage.getUser(userId).getFriends();
        List<User> usersFriends = new ArrayList<>();
        for (Long id : usersIds) {
            usersFriends.add(userStorage.getUsers().get(id));
        }
        return usersFriends;
    }

    public Set<User> getAllCommonFriends(Long userId, Long otherId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        if (!userStorage.getUsers().containsKey(otherId)) {
            throw new UserNotFoundException("Пользователь " + otherId + " не существует");
        }
        User user = userStorage.getUsers().get(userId);
        Set<Long> userFriends = user.getFriends();

        User other = userStorage.getUsers().get(otherId);
        Set<Long> otherFriends = other.getFriends();

        Set<User> commonFriends = new HashSet<>();
        for (Long userId1 : userFriends) {
            for (Long userId2 : otherFriends) {
                if (userId1.equals(userId2)) {
                    commonFriends.add(userStorage.getUsers().get(userId1));
                }
            }
        }
        return commonFriends;
    }

}
