package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.filmStorage = filmStorage;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;

    }

    public void addLike(int filmId, int userId) {
        if (inMemoryFilmStorage.getFilms().get(filmId).getLikes().contains(userId)) {
            throw new RuntimeException("Каждый пользователь может поставить лайк фильму только один раз");
        }
        inMemoryFilmStorage.getFilms().get(filmId).getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм " + filmId + " не существует");
        }
        if (inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не существует");
        }
        Film film = inMemoryFilmStorage.getFilms().get(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(Integer count) {
        List<Film> films = filmStorage.getAllFilms();
        Map<Film, Integer> filmLikes = new HashMap<>();
        for (Film film : films) {
            int likes = 0;
            for (Integer like : film.getLikes()) {
                if (like != null) {
                    likes = likes + like;
                }
            }
            filmLikes.put(film, likes);
        }
        films = filmLikes.entrySet().stream().sorted(Map.Entry.<Film, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey).collect(Collectors.toList());
        List<Film> filmsRes = new ArrayList<>();
        if (count == null) {
            count = films.size() > count ? films.size() : 10;
        }
        for (int i = 0; i < count; i++) {
            filmsRes.add(films.get(i));
        }
        return filmsRes;
    }
}
