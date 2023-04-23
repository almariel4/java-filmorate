package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Map<Long, Film> getFilms();

    List<Film> getAllFilms();

    Film getFilm(Long id);

    List<Genre> getAllGenres();

    Genre getGenre(int id);

    List<Mpa> getAllMpas();

    Mpa getMpa(int id);

/*    // Для внутренних тестов
    void resetId();*/
}
