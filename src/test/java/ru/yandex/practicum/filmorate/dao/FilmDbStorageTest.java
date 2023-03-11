package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void createFilm() {
        Film film = new Film(0L, "Вышка", "Только не смотри вниз!", List.of(new Genre(4, "Триллер")), LocalDate.of(2022, 8, 11), 107, new Mpa(3, "PG-13"), new HashSet<>());
        Optional<Film> testFilm = Optional.of(filmDbStorage.createFilm(film));

        assertThat(testFilm)
                .isPresent()
                .hasValueSatisfying(film1 ->
                        assertThat(film1).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Вышка")
                                .hasFieldOrPropertyWithValue("description", "Только не смотри вниз!")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2022, 8, 11))
                                .hasFieldOrPropertyWithValue("duration", 107)
                )
                .hasValueSatisfying(film1 ->
                        assertThat(film1.getGenres().get(0)).hasFieldOrPropertyWithValue("id", 4)
                                .hasFieldOrPropertyWithValue("name", "Триллер"))
                .hasValueSatisfying(film1 ->
                        assertThat(film1.getMpa().getId()).isEqualTo(3))
                .hasValueSatisfying(film1 ->
                        assertThat(film1.getMpa().getName()).isEqualTo("PG-13"));
    }

    @Test
    void updateFilm() {
        Film film = new Film(0L, "Марлоу", "Каждая тайна должна иметь свою звезду на Аллее славы", List.of(new Genre(4, "Триллер")), LocalDate.of(2023, 2, 23), 109, new Mpa(3, "PG-13"), new HashSet<>());
        film = filmDbStorage.createFilm(film);
        Optional<Film> testFilm = Optional.of(filmDbStorage.updateFilm(film));

        assertThat(testFilm)
                .isPresent()
                .hasValueSatisfying(film1 ->
                        assertThat(film1).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Марлоу")
                                .hasFieldOrPropertyWithValue("description", "Каждая тайна должна иметь свою звезду на Аллее славы")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2023, 2, 23))
                                .hasFieldOrPropertyWithValue("duration", 109));
    }

    @Test
    void getAllFilms() {
        Film film1 = new Film(0L, "Вышка", "Только не смотри вниз!", List.of(new Genre(4, "Триллер")), LocalDate.of(2022, 8, 11), 107, new Mpa(3, "PG-13"), new HashSet<>());
        Film film2 = new Film(1L, "Марлоу", "Каждая тайна должна иметь свою звезду на Аллее славы", List.of(new Genre(4, "Триллер")), LocalDate.of(2023, 2, 23), 109, new Mpa(3, "PG-13"), new HashSet<>());

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);
        List<Film> films = filmDbStorage.getAllFilms();

        assertThat(films)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    void getFilms() {
        Film film1 = new Film(0L, "Вышка", "Только не смотри вниз!", List.of(new Genre(4, "Триллер")), LocalDate.of(2022, 8, 11), 107, new Mpa(3, "PG-13"), new HashSet<>());
        Film film2 = new Film(0L, "Марлоу", "Каждая тайна должна иметь свою звезду на Аллее славы", List.of(new Genre(4, "Триллер")), LocalDate.of(2023, 2, 23), 109, new Mpa(3, "PG-13"), new HashSet<>());

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);
        Map<Long, Film> filmMap = filmDbStorage.getFilms();

        assertThat(filmMap)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    void getFilm() {
        Film film = new Film(0L, "Марлоу", "Каждая тайна должна иметь свою звезду на Аллее славы", List.of(new Genre(4, "Триллер")), LocalDate.of(2023, 2, 23), 109, new Mpa(3, "PG-13"), new HashSet<>());
        filmDbStorage.createFilm(film);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilm(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film1 ->
                        assertThat(film1).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Марлоу")
                                .hasFieldOrPropertyWithValue("description", "Каждая тайна должна иметь свою звезду на Аллее славы")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2023, 2, 23))
                                .hasFieldOrPropertyWithValue("duration", 109)
                );
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = filmDbStorage.getAllGenres();

        assertThat(genres)
                .isNotEmpty()
                .hasSize(6);
    }

    @Test
    void getGenre() {
        Optional<Genre> genreOptional = Optional.ofNullable(filmDbStorage.getGenre(1));

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1))
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
                );

    }

    @Test
    void getAllMpas() {
        List<Mpa> mpas = filmDbStorage.getAllMpas();

        assertThat(mpas)
                .isNotEmpty()
                .hasSize(5);
    }

    @Test
    void getMpa() {
        Optional<Mpa> mpaOptional = Optional.ofNullable(filmDbStorage.getMpa(3));

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 3))
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "PG-13")
                );
    }

    @Test
    void addLike() {
        Film film = new Film(0L, "Марлоу", "Каждая тайна должна иметь свою звезду на Аллее славы", List.of(new Genre(4, "Триллер")), LocalDate.of(2023, 2, 23), 109, new Mpa(3, "PG-13"), new HashSet<>());
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());
        user = userDbStorage.createUser(user);
        film = filmDbStorage.createFilm(film);
        filmDbStorage.addLike(film.getId(), user.getId());

        assertThat(filmDbStorage.getLikes(film.getId()))
                .hasSize(1)
                .contains(1);
    }

    @Test
    void removeLike() {
        Film film = new Film(0L, "Марлоу", "Каждая тайна должна иметь свою звезду на Аллее славы", List.of(new Genre(4, "Триллер")), LocalDate.of(2023, 2, 23), 109, new Mpa(3, "PG-13"), new HashSet<>());
        User user = new User(0L, "user@mail.ru", "almariel", "Anna", LocalDate.of(1990, 3, 9), new HashMap<>());

        film = filmDbStorage.createFilm(film);
        user = userDbStorage.createUser(user);
        filmDbStorage.addLike(film.getId(), user.getId());

        assertThat(filmDbStorage.getLikes(film.getId()))
                .hasSize(1)
                .contains(1);

        filmDbStorage.removeLike(film.getId(), user.getId());

        assertThat(filmDbStorage.getLikes(film.getId()))
                .hasSize(0);
    }


}