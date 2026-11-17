package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({GenreDbStorage.class, GenreRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbc;

    @Test
    public void testGetAll() {
        Collection<Genre> genres = genreStorage.getAll();

        assertEquals(6, genres.size());
    }

    @Test
    public void testGetById() {
        Genre genre = genreStorage.getById(1).orElseThrow();

        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getDescription());
    }

    @Test
    public void testGetByFilm() {
        int filmId = 1000;

        jdbc.update("INSERT INTO films(id,title,description,duration,release_date,mpa_id)"
            + "VALUES (?,?,?,?,?,?)", filmId, "Фильм", "Описание", 150, "1990-01-11", 1);
        jdbc.update("INSERT INTO film_genre(genre_id, film_id) VALUES(?,?)", 1, filmId);
        jdbc.update("INSERT INTO film_genre(genre_id, film_id) VALUES(?,?)", 2, filmId);

        Collection<Genre> genres = genreStorage.getByFilmId(1000);

        assertEquals(2, genres.size());
    }
}
