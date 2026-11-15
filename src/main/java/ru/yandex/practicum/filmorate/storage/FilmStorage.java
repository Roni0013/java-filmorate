package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> list();

    int add(Film film);

    Optional<Film> getById(int id);

    void update(Film film);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    int addGenresToFilm(int filmId, List<Genre> genres);

    int deleteGenresFromFilm(int filmId);

    Collection<Film> popular(int limit);

}
