package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaCode;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Film.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("title"))
            .description(rs.getString("description"))
            .releaseDate(rs.getDate("release_date").toLocalDate())
            .duration(rs.getInt("duration"))
            .mpa(Mpa.builder().id(rs.getInt("mpa_id"))
                .code(MpaCode.getCodeByString(rs.getString("mpa_code"))).build())
            .build();
    }
}
