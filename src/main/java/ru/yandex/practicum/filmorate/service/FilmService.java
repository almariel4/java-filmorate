package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId, Long userId) {
        if (filmStorage.getFilms().get(filmId).getLikes().contains(userId)) {
            throw new RuntimeException("Каждый пользователь может поставить лайк фильму только один раз");
        }
        filmStorage.getFilms().get(filmId).getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм " + filmId + " не существует");
        }
        if (!filmStorage.getFilms().get(filmId).getLikes().contains(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        Film film = filmStorage.getFilms().get(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(Long count) {

        List<Film> collection = filmStorage.getAllFilms()
                .stream()
                .sorted((f0, f1) -> compare(f0, f1))
                .limit(Objects.requireNonNullElse(count, 10L))
                .collect(Collectors.toList());
        System.out.println(collection);
        return collection;
    }

    public int compare(Film f0, Film f1) {
        return f1.getLikes().size() - (f0.getLikes().size());
    }
}
