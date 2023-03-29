package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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
public class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    // без @WebMvcTest не находятся бины для objectMapper
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage.getFilms().clear();
        filmStorage.resetId();
    }

    @SneakyThrows
    @Test
    void createFilm() {
        Film film = new Film(0L, "Film name", "Description", LocalDate.now(), 120, new HashSet<>());
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film.setId(1L);
        assertEquals(objectMapper.writeValueAsString(film), response);
    }

    @SneakyThrows
    @Test
    void updateFilm() {
        Film film = new Film(0L, "Film name", "Description", LocalDate.now(), 120, new HashSet<>());

        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film.setId(1L);
        film.setName("Update Film name");
        String response2 = mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(film), response2);
    }

    @SneakyThrows
    @Test
    void filmNameCantBeBlank() {
        Film film = new Film(0L, " ", "Description", LocalDate.now(), 120, new HashSet<>());
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void filmDescriptionMustBeLessThen200() {
        Film film = new Film(0L, "Film name",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                LocalDate.now(), 120, new HashSet<>());
        film.setName("Update Film name");

        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void filmDurationMustBeMoreThanZero() {
        Film film = new Film(0L, "Film Name", "Description", LocalDate.now(), -10, new HashSet<>());
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void filmDateMustBeLaterThan28_12_1895() {
        Film film = new Film(0L, "Film Name", "Description", LocalDate.of(1860, 12, 28), 120, new HashSet<>());
        String response = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void getAllFilms() {
        Film film = new Film(0L, "Film Name", "Description", LocalDate.now(), 100, new HashSet<>());
        Film film2 = new Film(0L, "Film Name", "Description", LocalDate.now(), 110, new HashSet<>());
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
