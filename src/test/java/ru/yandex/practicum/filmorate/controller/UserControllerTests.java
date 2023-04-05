package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage.getUsers().clear();
        userStorage.resetId();
    }

    @SneakyThrows
    @Test
    void createUser() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashSet<>());
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user.setId(1L);
        assertEquals(objectMapper.writeValueAsString(user), response);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashSet<>());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        user.setName("Updated user Name");
        user.setId(1L);
        String response2 = mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), response2);
    }

    @SneakyThrows
    @Test
    void emailShouldMatchPattern() {
        User user = new User(0L, "qwe", "almariel1", "Anna", LocalDate.of(1990, 3, 9), new HashSet<>());

        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void emailCantBeBlank() {
        User user = new User(0L, "", "almariel1", "Anna", LocalDate.of(1990, 3, 9), new HashSet<>());
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void birthdayCantBeInFuture() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(2024, 3, 9), new HashSet<>());
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void loginCantBeBlank() {
        User user = new User(0L, "user@mail.ru", "", "Anna", LocalDate.of(1990, 3, 9), new HashSet<>());
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashSet<>());
        User user2 = new User(0L, "kristina@mail.ru", "kristina", "Kristina", LocalDate.of(2021, 7, 23), new HashSet<>());
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(String.valueOf((user))));
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(String.valueOf((user2))));

        assertEquals(users.size(), 2);
    }
}
