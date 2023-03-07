package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//    FIXME: не происходит автосвязывания mockMvc и objectMapper => не могу проверить внутренние тесты.

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().toEpochDay() < LocalDate.of(1895, 12, 28).toEpochDay()) {
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        }
        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.info("Добавлен фильм id='{}', name='{}'", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new RuntimeException("Фильм с id=" + film.getId() + " отсутствует в списке пользователей");
        }
        films.remove(film.getId());
        films.put(film.getId(), film);
        log.info("Фильм id='{}', name='{}' был обновлен", film.getId(), film.getName());
        return film;
    }
}
