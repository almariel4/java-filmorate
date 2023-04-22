package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@AllArgsConstructor
@Component
@Slf4j
public class UserDbStorage implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        if (user.getName().isBlank()) {
            parameters.put("name", user.getLogin());
            user.setName(user.getLogin());
        } else {
            parameters.put("name", user.getName());
        }
        parameters.put("birthday", user.getBirthday());

        Number insertedId = simpleJdbcInsert.executeAndReturnKey(parameters);
        user.setId(insertedId.longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!checkIfExistsUser(user.getId())) {
            throw new UserNotFoundException("Пользователь с id=" + user.getId() + " отсутствует в списке пользователей");
        }
        jdbcTemplate.update("UPDATE USERS " +
                "SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ?" +
                "WHERE USER_ID = ?", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT  u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM USERS AS u ";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    public boolean checkIfExistsUser(Long userId) {
        String sql = "SELECT COUNT(*) FROM USERS " +
                "WHERE USER_ID = ?";
        Long size = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return size == 1;
    }

    @Override
    public Map<Long, User> getUsers() {
        List<User> users = getAllUsers();
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }
        return userMap;
    }

    @Override
    public User getUser(Long id) {
        String sql = "SELECT  u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM USERS AS u " +
                "WHERE u.USER_ID = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new UserNotFoundException("Пользователя с id=" + id + " отсутствует в списке пользователей");
        }
        return user;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User newUser = new User();
        newUser.setId(resultSet.getLong("user_id"));
        newUser.setEmail(resultSet.getString("email"));
        newUser.setLogin(resultSet.getString("login"));
        newUser.setName(resultSet.getString("name"));
        newUser.setBirthday(resultSet.getDate("birthday").toLocalDate());
        log.info(String.valueOf(newUser));
        return newUser;
    }

    public void addToFriends(Long userId, Long friendId) {
        jdbcTemplate.update("MERGE INTO FRIENDS (USER_ID, FRIEND_ID) KEY (USER_ID, FRIEND_ID) " +
                "VALUES ( ?, ? )", userId, friendId);
    }

    public void removeFromFriends(Long userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM FRIENDS " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?", userId, friendId);
    }

    public List<User> getAllFriends(Long userId) {
        String sql = "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM users AS u " +
                "WHERE u.USER_ID IN " +
                "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }
}
