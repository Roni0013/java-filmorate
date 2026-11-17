package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String ALL_QUERY =
        "SELECT f.id,title,description,release_date,duration,mpa_id,mpa.code mpa_code FROM films f "
            + "JOIN mpa ON mpa.id=f.mpa_id";
    private static final String BY_ID_QUERY =
        "SELECT f.id,title,description,release_date,duration,mpa_id,mpa.code mpa_code "
            + "FROM films f JOIN mpa ON mpa.id=f.mpa_id WHERE f.id=?";
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
            + "FROM films f JOIN mpa ON mpa.id=f.mpa_id JOIN likes l ON l.film_id=f.id "
            + "GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, GenreStorage genreStorage, MpaStorage mpaStorage) {
        super(jdbc, mapper);
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Film> getAll() {
        return findMany(ALL_QUERY);
    }

    @Override
    public int add(Film film) {
        mpaThrowIfNotExists(film.getMpa().getId());
        film.getGenres().stream().map(Genre::getId).forEach(this::genreThrowIfNotExists);
        int filmId = insert(INSERT_QUERY, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
            film.getDuration(), film.getMpa().getId());
        film.getGenres().forEach(genre -> update(ADD_GENRE, filmId, genre.getId()));
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
        mpaThrowIfNotExists(film.getMpa().getId());
        film.getGenres().stream().map(Genre::getId).forEach(this::genreThrowIfNotExists);

        int filmId = film.getId();
        update(UPDATE_QUERY, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
            film.getDuration(), film.getMpa().getId(), filmId);

        update(DELETE_GENRE, filmId);
        film.getGenres().forEach(genre -> update(ADD_GENRE, filmId, genre.getId()));
    }

    @Override
    public void addLike(Film film, User user) {
        update(INSERT_LIKE, film.getId(), user.getId());
    }

    public void removeLike(Film film, User user) {
        update(DELETE_LIKE, film.getId(), user.getId());
    }

    @Override
    public Collection<Film> popular(int limit) {
        return findMany(POPULAR_QUERY, limit);
    }

    private void mpaThrowIfNotExists(int id) {
        mpaStorage.getById(id).orElseThrow(() -> {
            log.error("mpa id = {} не найден", id);
            return new NotFoundException(String.format("Неверно указано ограничение для фильма, id=%d", id));
        });
    }

    private void genreThrowIfNotExists(int id) {
        genreStorage.getById(id).orElseThrow(() -> {
            log.error("genre id = {} не найден", id);
            return new NotFoundException(String.format("Неверно указан жанр фильма, id=%d", id));
        });
    }
}
