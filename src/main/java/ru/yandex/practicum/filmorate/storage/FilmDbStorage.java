package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String ALL_QUERY = "SELECT f.id,title,description,release_date,duration,mpa_id,mpa.code mpa_code from films f JOIN mpa ON mpa.id=f.mpa_id";
    private static final String BY_ID_QUERY =
        "SELECT f.id,title,description,release_date,duration,mpa_id,mpa.code mpa_code FROM films f JOIN mpa ON mpa.id=f.mpa_id WHERE f.id=?";
    private static final String INSERT_QUERY =
        "INSERT INTO films(title,description,release_date,duration,mpa_id) VALUES (?,?,?,?,?)";
    private static final String UPDATE_QUERY =
        "UPDATE films SET title=?,description=?,release_date=?,duration=?,mpa_id=? WHERE id=?";
    private static final String INSERT_LIKE = "INSERT INTO likes(film_id, user_id) values(?,?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id=? AND user_id=?";
    private static final String ADD_GENRE = "INSERT INTO film_genre(film_id, genre_id) VALUES (?,?)";
    private static final String DELETE_GENRE = "DELETE FROM film_genre WHERE film_id=?";
    private static final String POPULAR_QUERY =
        "SELECT f.id,f.title,f.description,f.release_date,f.duration,f.mpa_id,mpa.code mpa_code "
            + "FROM films f JOIN mpa ON mpa.id=f.mpa_id JOIN likes l ON l.film_id=f.id GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

    private final GenreStorage genreStorage;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, GenreStorage genreStorage) {
        super(jdbc, mapper);
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> list() {
        return findMany(ALL_QUERY);
    }

    @Override
    public int add(Film film) {
        int filmId = insert(INSERT_QUERY, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
            film.getDuration(), film.getMpa().getId());
        log.info("Новый фильм добавлен, id = {}", filmId);
        return filmId;
    }

    @Override
    public Optional<Film> getById(int id) {
        Optional<Film> filmOptional = findOne(BY_ID_QUERY, id);
        if (filmOptional.isEmpty()) {
            return Optional.empty();
        }
        Set<Genre> genres = new HashSet<>(genreStorage.getByFilmId(id));
        Film film = filmOptional.get().toBuilder().genres(genres).build();
        return Optional.of(film);
    }

    @Override
    public void update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
            film.getDuration(), film.getMpa().getId(), film.getId());
    }

    @Override
    public void addLike(Film film, User user) {
        update(INSERT_LIKE, film.getId(), user.getId());
    }

    public void removeLike(Film film, User user) {
        update(DELETE_LIKE, film.getId(), user.getId());
    }

    @Override
    public int addGenresToFilm(int filmId, List<Genre> genres) {
        int count = 0;
        for (Genre genre : genres) {
            update(ADD_GENRE, filmId, genre.getId());
            count++;
        }
        log.info("Добавлено {} жанра для фильма id={}", count, filmId);
        return count;
    }

    @Override
    public int deleteGenresFromFilm(int filmId) {
        int count = update(DELETE_GENRE, filmId);
        log.info("Удалено {} жанра для фильма id={}", count, filmId);
        return count;
    }

    @Override
    public Collection<Film> popular(int limit) {
        return findMany(POPULAR_QUERY, limit);
    }
}
