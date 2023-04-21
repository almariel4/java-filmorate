package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;

    private Long generateId() {
        return ++id;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Пользователь с " + filmId + "не найден");
        }
        return films.get(filmId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Genre getGenre(int id) {
        return null;
    }

    @Override
    public List<Mpa> getAllMpas() {
        return null;
    }

    @Override
    public Mpa getMpa(int id) {
        return null;
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().toEpochDay() < LocalDate.of(1895, 12, 28).toEpochDay()) {
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм id='{}', name='{}'", film.getId(), film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с id=" + film.getId() + " отсутствует в списке пользователей");
        }
        films.remove(film.getId());
        films.put(film.getId(), film);
        log.info("Фильм id='{}', name='{}' был обновлен", film.getId(), film.getName());
        return film;
    }
}
