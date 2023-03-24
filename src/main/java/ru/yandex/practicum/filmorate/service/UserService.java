package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage userStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = userStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

//    FIXME: Method threw 'java.lang.StackOverflowError' exception. Cannot evaluate ru.yandex.practicum.filmorate.model.User.toString()
    public User addToFriends(int userId, int friendId) {
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new UserNotFoundException("Пользователь " + friendId + " не существует");
        }
        User user = inMemoryUserStorage.getUsers().get(userId);
        User friend = inMemoryUserStorage.getUsers().get(friendId);
        Set<User> friends = user.getFriends();
        friends.add(friend);
        user.setFriends(friends);
        inMemoryUserStorage.getUsers().put(userId, user);

        Set<User> friends2 = friend.getFriends();
        friends2.add(user);
        friend.setFriends(friends2);
        inMemoryUserStorage.getUsers().put(friendId, friend);

        return user;
    }

    public User removeFromFriends(int userId, int friendId) {
        User user = inMemoryUserStorage.getUsers().get(userId);
        User friend = inMemoryUserStorage.getUsers().get(friendId);
        Set<User> friends = user.getFriends();
        friends.remove(friend);
        user.setFriends(friends);
        inMemoryUserStorage.getUsers().put(userId, user);

        Set<User> friends2 = friend.getFriends();
        friends2.remove(user);
        friend.setFriends(friends2);
        inMemoryUserStorage.getUsers().put(friendId, friend);

        return user;
    }

    public Set<User> getAllFriends(int userId) {
        return inMemoryUserStorage.getUsers().get(userId).getFriends();
    }

    public Set<User> getAllCommonFriends(int userId, int otherId) {
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(otherId)) {
            throw new UserNotFoundException("Пользователь " + otherId + " не существует");
        }
        User user = inMemoryUserStorage.getUsers().get(userId);
        Set<User> userFriends = user.getFriends();

        User other = inMemoryUserStorage.getUsers().get(otherId);
        Set<User> otherFriends = other.getFriends();

        Set<User> commonFriends = new HashSet<>();
        for (User user1 : userFriends) {
            for (User user2 : otherFriends) {
                if (user1.getId() == user2.getId()) {
                    commonFriends.add(user1);
                }
            }
        }
        return commonFriends;
    }

}
