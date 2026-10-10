package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> list() {
        return filmStorage.list();
    }

    public Film getById(int id) {
        return getFilm(id);
    }

    public Film create(Film film) {
        int id = filmStorage.add(film);
        log.info("Фильм {} добавлен", film.getName());
        return getFilm(id);
    }

    public Film update(Film film) {
        Film existsFilm = getFilm(film.getId());
        existsFilm = existsFilm.toBuilder().name(film.getName()).description(film.getDescription())
            .releaseDate(film.getReleaseDate()).duration(film.getDuration()).build();
        filmStorage.update(existsFilm);
        log.info("Фильм {} обновлен", film.getName());
        return existsFilm;
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);
        film.addLike(user);
        log.info("Пользователь {} поставил лайк к фильму {}", user.getLogin(), film.getName());
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);
        film.removeLike(user);
        log.info("Пользователь {} убрал лайк к фильму {}", user.getLogin(), film.getName());
    }

    public Collection<Film> getMostPopular(int count) {
        return filmStorage.list().stream().sorted(new Film.PopulationComparator().reversed()).limit(count).toList();
    }

    private Film getFilm(int id) {
        return filmStorage.getById(id).orElseThrow(() -> {
            log.error("Фильм id = {} не найден", id);
            return new NotFoundException(String.format("Фильм id = %d не найден", id));
        });
    }

    private User getUser(int id) {
        return userStorage.getById(id).orElseThrow(() -> {
            log.error("Пользователь id = {} не найден", id);
            return new NotFoundException(String.format("Пользователь id = %d не найден", id));
        });
    }
}
