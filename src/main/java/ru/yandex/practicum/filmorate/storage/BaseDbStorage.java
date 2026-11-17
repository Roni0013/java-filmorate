package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(query, mapper, params));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected int update(String query, Object... params) {
        log.debug("Обновление записи {}", Arrays.toString(params));
        return jdbc.update(query, params);
    }

    protected int insert(String query, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> preparedStatement(query, con, params), keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id == null) {
            log.error("Ошибка сохранения");
            throw new InternalServerException("Не удалось сохранить данные");
        }
        return id;
    }

    private PreparedStatement preparedStatement(String query, Connection con, Object... params) throws SQLException {
        log.debug("Подготовка PreparedStatement для {}", query);
        PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        log.debug("PreparedStatement подготовлен {}", ps.toString());
        for (int idx = 0; idx < params.length; idx++) {
            ps.setObject(idx + 1, params[idx]);
        }
        log.info(ps.toString());
        return ps;
    }
}
