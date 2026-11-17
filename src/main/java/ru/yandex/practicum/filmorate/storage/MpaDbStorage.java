package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {
    private static final String ALL_QUERY = "SELECT id,code FROM mpa";
    private static final String BY_ID_QUERY = "SELECT id,code FROM mpa WHERE id=?";

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getAll() {
        return findMany(ALL_QUERY);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        return findOne(BY_ID_QUERY, id);
    }
}
