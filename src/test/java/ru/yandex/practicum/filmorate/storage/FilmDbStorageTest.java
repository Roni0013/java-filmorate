package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class, GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    public void setUp() {
        jdbc.update("INSERT INTO films(id,title,description,duration,release_date,mpa_id)"
            + "VALUES (?,?,?,?,?,?)", 1000, "Фильм", "Описание", 150, "1990-01-11", 1);
    }

    @Test
    public void addFilm() {
        String name = "Фильм";
        String description = "Описание";
        int duration = 150;
        Mpa mpa = Mpa.builder().id(1).build();
        Film film = Film.builder().name(name).releaseDate(LocalDate.now()).description(description).duration(duration)
            .mpa(mpa).build();

        int addedFilmId = filmDbStorage.add(film);

        Film addedFilm = filmDbStorage.getById(addedFilmId).orElseThrow();
        assertEquals(name, addedFilm.getName());
        assertEquals(description, addedFilm.getDescription());
        assertEquals(duration, addedFilm.getDuration());
        assertEquals(1, addedFilm.getMpa().getId());
        assertEquals(MpaCode.G, addedFilm.getMpa().getCode());
    }

    @Test
    public void updateFilm() {
        int filmId = 1000;
        String name = "Новое название";
        String description = "Новое описание";
        int duration = 110;
        String releaseDate = "2000-01-01";
        Mpa mpa = Mpa.builder().id(2).build();
        Film filmToUpdate = Film.builder().id(filmId).name(name).description(description).duration(duration)
            .releaseDate(LocalDate.parse(releaseDate)).mpa(mpa).build();

        filmDbStorage.update(filmToUpdate);

        Film updatedFilm = filmDbStorage.getById(filmId).orElseThrow();
        assertEquals(name, updatedFilm.getName());
        assertEquals(description, updatedFilm.getDescription());
        assertEquals(duration, updatedFilm.getDuration());
        assertEquals(releaseDate, updatedFilm.getReleaseDate().toString());
        assertEquals(2, updatedFilm.getMpa().getId());
    }

    @Test
    public void addLike() {
        int filmId = 1000;
        int userId = 2000;
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", userId, "testUser", "Иванов Иван", "test@test.tets", "2026-11-11");
        User user = User.builder().id(userId).build();
        Film film = Film.builder().id(filmId).build();

        filmDbStorage.addLike(film, user);

        Collection<User> userLikes = userDbStorage.getUsersByFilmId(filmId);
        assertEquals(1, userLikes.size());

    }

    @Test
    public void removeLike() {
        int filmId = 1000;
        int userId = 2000;

        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", userId, "testUser", "Иванов Иван", "test@test.tets", "2026-11-11");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES(?,?)", userId, filmId);
        User user = User.builder().id(userId).build();
        Film film = Film.builder().id(filmId).build();

        filmDbStorage.removeLike(film, user);

        Collection<User> userLikes = userDbStorage.getUsersByFilmId(filmId);
        assertTrue(userLikes.isEmpty());
    }

    @Test
    public void addGenresToFilm() {
        int filmId = 1000;
        Genre genre = new Genre(1, "");
        List<Genre> genres = List.of(genre);

        filmDbStorage.addGenresToFilm(filmId, genres);

        Collection<Genre> filmGenres = filmDbStorage.getById(filmId).orElseThrow().getGenres();
        assertEquals(1, filmGenres.size());
    }

    @Test
    public void deleteGenresToFilm() {
        int filmId = 1000;
        jdbc.update("INSERT INTO film_genre(film_id,genre_id) VALUES(?,?)", filmId, 2);

        filmDbStorage.deleteGenresFromFilm(filmId);

        Collection<Genre> filmGenres = filmDbStorage.getById(filmId).orElseThrow().getGenres();
        assertTrue(filmGenres.isEmpty());
    }

    @Test
    public void popular() {
        jdbc.update("INSERT INTO films(id,title,description,duration,release_date,mpa_id)"
            + "VALUES (?,?,?,?,?,?)", 1010, "Фильм1", "Описание1", 160, "1990-01-11", 1);
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", 2000, "testUser", "Иванов Иван", "test@test.tesе", "2026-11-11");
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", 2010, "testUser1", "Петров Петр", "test@test.tets", "2026-11-11");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES(?,?)", 2000, 1000);
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES(?,?)", 2000, 1010);
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES(?,?)", 2010, 1010);

        List<Film> popularFilms = filmDbStorage.popular(5).stream().toList();

        assertEquals(1010, popularFilms.getFirst().getId());
    }
}
