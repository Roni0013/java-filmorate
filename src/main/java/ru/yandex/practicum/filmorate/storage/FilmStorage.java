package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAll();

    int add(Film film);

    Optional<Film> getById(int id);

    void update(Film film);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    Collection<Film> popular(int limit);
}
