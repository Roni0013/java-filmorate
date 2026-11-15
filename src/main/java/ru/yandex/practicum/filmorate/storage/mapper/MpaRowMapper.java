package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<Mpa> {
    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(
            rs.getInt("id"),
            getCodeByString(rs.getString("code")),
            rs.getString("description")
        );
    }

    private Mpa.Code getCodeByString(String dbCode) {
        return switch (dbCode) {
            case "G" -> Mpa.Code.G;
            case "PG" -> Mpa.Code.PG;
            case "PG-13" -> Mpa.Code.PG13;
            case "R" -> Mpa.Code.R;
            case "NC-17" -> Mpa.Code.NC17;
            default ->
                throw new InternalServerException(String.format("Ошибка данных в категориях фильмов %s", dbCode));
        };
    }
}
