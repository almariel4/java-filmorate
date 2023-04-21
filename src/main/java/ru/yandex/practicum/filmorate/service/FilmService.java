package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public void addLike(Long filmId, Long userId) {
        filmDbStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!filmDbStorage.getFilms().containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм " + filmId + " не существует");
        }
        if (!userDbStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        filmDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(Long count) {

        List<Film> collection = filmDbStorage.getAllFilms()
                .stream()
                .sorted((f0, f1) -> compare(f0, f1))
                .limit(Objects.requireNonNullElse(count, 10L))
                .collect(Collectors.toList());
        log.debug(collection.toString());
        return collection;
    }

    private int compare(Film f0, Film f1) {
        return filmDbStorage.getLikes(f1.getId()).size() - filmDbStorage.getLikes(f0.getId()).size();
    }
}
