package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        FilmController.getFilms().clear();
    }

    @SneakyThrows
    @Test
    void createFilm() {
        Film film = new Film(0, "Film name", "Description", LocalDate.now(), 120);
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film.setId(1);
        System.out.println(objectMapper.writeValueAsString(film));
        System.out.println(response);
        assertEquals(objectMapper.writeValueAsString(film), response);
    }

    @SneakyThrows
    @Test
    void updateFilm() {
        Film film = new Film(0, "Film name", "Description", LocalDate.now(), 120);

        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film.setId(1);
        film.setName("Update Film name");
        String response2 = mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(film));
        System.out.println(response);
        System.out.println(response2);
        assertEquals(objectMapper.writeValueAsString(film), response2);
    }

    @SneakyThrows
    @Test
    void filmNameCantBeBlank() {
        Film film = new Film(0, " ", "Description", LocalDate.now(), 120);
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(film));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void filmDescriptionMustBeLessThen200() {
        Film film = new Film(0, "Film name",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                LocalDate.now(), 120);
        film.setName("Update Film name");

        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(film));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void filmDurationMustBeMoreThanZero() {
        Film film = new Film(0, "Film Name", "Description", LocalDate.now(), -10);
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(film));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void filmDateMustBeLaterThan28_12_1895() {
        Film film = new Film(0, "Film Name", "Description", LocalDate.of(1860, 12, 28), 120);
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(objectMapper.writeValueAsString(film));
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void getAllFilms() {
        Film film = new Film(0, "Film Name", "Description", LocalDate.now(), 100);
        Film film2 = new Film(0, "Film Name", "Description", LocalDate.now(), 110);
        List<Film> films = new ArrayList<>();
        films.add(film);
        films.add(film2);

        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(String.valueOf((film))));
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(String.valueOf((film2))));

        assertEquals(films.size(), 2);
    }
}
