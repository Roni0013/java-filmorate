package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String ALL_QUERY = "SELECT id,title FROM genre";
    private static final String BY_ID_QUERY = "SELECT id,title FROM genre WHERE id=?";
    private static final String BY_FILM_QUERY =
        "SELECT g.id,g.title FROM genre g JOIN film_genre fg ON fg.genre_id=g.id WHERE fg.film_id=?";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> list() {
        return findMany(ALL_QUERY);
    }

    @Override
    public Optional<Genre> getById(int id) {
        return findOne(BY_ID_QUERY, id);
    }

    @Override
    public Collection<Genre> getByFilmId(int filmId) {
        return findMany(BY_FILM_QUERY, filmId);
    }
}
