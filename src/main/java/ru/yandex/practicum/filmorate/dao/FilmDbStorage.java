package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Primary
@AllArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        if (film.getReleaseDate().toEpochDay() < LocalDate.of(1895, 12, 28).toEpochDay()) {
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        } else {
            parameters.put("releasedate", film.getReleaseDate());
        }
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number insertedId = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId(insertedId.longValue());

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) " +
                        "VALUES ( ?, ? )", film.getId(), genre.getId());
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!checkIfExistsFilm(film.getId())) {
            throw new FilmNotFoundException("Фильм с id=" + film.getId() + " отсутствует в списке фильмов");
        }
        jdbcTemplate.update("UPDATE FILMS " +
                "SET NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ? , MPA_ID = ?" +
                "WHERE FILM_ID = ?", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        Set<Genre> genres = new HashSet<>(film.getGenres());

        jdbcTemplate.update("DELETE FROM FILM_GENRE " +
                "WHERE FILM_ID = ?", film.getId());

        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) " +
                        "VALUES ( ?, ? )", film.getId(), genre.getId());
            }

            film.setGenres(getGenresByFilmId(film.getId()));
        }
        return film;
    }

    public boolean checkIfExistsFilm(Long filmId) {
        String sql = "SELECT COUNT(*) FROM FILMS " +
                "WHERE FILM_ID = ?";
        Long size = jdbcTemplate.queryForObject(sql, Long.class, filmId);
        return size == 1;
    }

    private List<Genre> getGenresByFilmId(long filmId) {
        String sql = "SELECT G.GENRE_ID, G.GENRE_NAME FROM GENRES AS G " +
                "JOIN FILM_GENRE FG on G.GENRE_ID = FG.GENRE_ID " +
                "WHERE FG.FILM_ID = ? " +
                "ORDER BY GENRE_ID ASC";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.MPA_ID, m.MPA_NAME, fg.GENRE_ID " +
                "FROM FILMS AS f " +
                "JOIN MPA M on f.MPA_ID = M.MPA_ID " +
                "LEFT JOIN FILM_GENRE FG on f.FILM_ID = FG.FILM_ID";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Map<Long, Film> getFilms() {
        List<Film> films = getAllFilms();
        Map<Long, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        return filmMap;
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.MPA_ID, m.MPA_NAME  " +
                "FROM FILMS AS f " +
                "JOIN MPA m on m.MPA_ID = f.MPA_ID " +
                "WHERE f.FILM_ID = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new FilmNotFoundException("Фильм с id=" + id + " отсутствует в списке фильмов");
        }
        return film;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("releaseDate").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")));
        film.setGenres(getGenresByFilmId(film.getId()));
        return film;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT GENRE_ID, GENRE_NAME FROM GENRES";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Genre getGenre(int id) {
        String sql = "SELECT GENRE_ID, GENRE_NAME FROM GENRES WHERE GENRE_ID = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new GenreNotFoundException("Жанр с id=" + id + " отсутствует в списке жанров");
        }
        return genre;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("genre_name"));
        return genre;
    }

    @Override
    public List<Mpa> getAllMpas() {
        String sql = "SELECT MPA_ID, MPA_NAME FROM MPA";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    @Override
    public Mpa getMpa(int id) {
        String sql = "SELECT MPA_ID, MPA_NAME FROM MPA WHERE MPA_ID = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new MpaNotFoundException("Рейтинг с id=" + id + " отсутствует в списке рейтингов");
        }
        return mpa;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        return mpa;
    }

    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update("MERGE INTO LIKES (FILM_ID, USER_ID) KEY (FILM_ID, USER_ID) " +
                "VALUES ( ?, ? )", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update("DELETE FROM LIKES " +
                "WHERE FILM_ID = ? AND USER_ID = ?", filmId, userId);
    }

    public List<Integer> getLikes(long filmId) {
        String sql = "SELECT USER_ID " +
                "FROM LIKES " +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToLikes, filmId);
    }

    private int mapRowToLikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }
}
