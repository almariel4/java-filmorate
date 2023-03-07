package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void createUser() {
        User user = new User("mail@mail.ru", "Login", "Name", LocalDate.of(1990, 3, 9));
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(response);
        assertEquals(objectMapper.writeValueAsString(user), response);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        User user = new User("user@mail.ru", "almariel1", "Anna", LocalDate.of(1990, 3, 9));

        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        user.setName("Updated user Name");
        String response2 = mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(response);
        assertEquals(objectMapper.writeValueAsString(user), response);
    }

    @SneakyThrows
    @Test
    void emailShouldMatchPattern() {
        User user = new User("qwe", "almariel1", "Anna", LocalDate.of(1990, 3, 9));
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is5xxServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void emailCantBeBlank() {
        User user = new User("", "almariel1", "Anna", LocalDate.of(1990, 3, 9));
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is5xxServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void loginCantBeBlank() {
        User user = new User("user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9));
        User user2 = new User("user@mail.ru", "kristina", "Kristina", LocalDate.of(2021, 7, 23));
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String response2 = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().is5xxServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void birthdayCantBeInFuture() {
        User user = new User("user@mail.ru", "almariel", "Anna", LocalDate.of(2024, 3, 9));
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is5xxServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void loginCantBeBlanc() {
        User user = new User("user@mail.ru", "", "Anna", LocalDate.of(1990, 3, 9));
        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is5xxServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        User user = new User("user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9));
        User user2 = new User("kristina@mail.ru", "kristina", "Kristina", LocalDate.of(2021, 7, 23));
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
