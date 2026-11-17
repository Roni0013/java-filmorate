package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.FilmListDTO;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.request.FilmCreateRequest;
import ru.yandex.practicum.filmorate.request.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<FilmListDTO> getAll() {
        return filmStorage.getAll().stream().map(FilmMapper::mapToFilmListDTO).toList();
    }

    public FilmDTO getById(int id) {
        return FilmMapper.mapToFilmDTO(getFilm(id));
    }

    public FilmDTO create(FilmCreateRequest filmRequest) {
        Set<Genre> genres = new HashSet<>();
        filmRequest.getGenres().forEach(genreRequest -> genres.add(Genre.builder().id(genreRequest.getId()).build()));

        Film film = Film.builder().name(filmRequest.getName()).description(filmRequest.getDescription())
            .duration(filmRequest.getDuration()).releaseDate(filmRequest.getReleaseDate())
            .mpa(Mpa.builder().id(filmRequest.getMpa().getIdOrDefault()).build())
            .genres(genres)
            .build();
        int createdFilmId = filmStorage.add(film);
        log.info("Фильм {} добавлен", film.getName());
        return FilmMapper.mapToFilmDTO(getFilm(createdFilmId));
    }

    public FilmDTO update(FilmUpdateRequest filmRequest) {
        int filmId = filmRequest.getId();
        Film existsFilm = getFilm(filmId);
        Set<Genre> newGenres = new HashSet<>();
        filmRequest.getGenres()
            .forEach(genreRequest -> newGenres.add(Genre.builder().id(genreRequest.getId()).build()));

        existsFilm = existsFilm.toBuilder().name(filmRequest.getName()).description(filmRequest.getDescription())
            .releaseDate(filmRequest.getReleaseDate()).duration(filmRequest.getDuration())
            .mpa(Mpa.builder().id(filmRequest.getMpa().getIdOrDefault()).build())
            .genres(newGenres).build();

        filmStorage.update(existsFilm);
        log.info("Фильм {} обновлен", filmRequest.getName());
        return FilmMapper.mapToFilmDTO(getFilm(filmId));
    }

    public void addLike(int filmId, int userId) {
        User user = getUser(userId);
        boolean isLiked = new HashSet<>(userStorage.getUsersByFilmId(filmId)).add(user);
        if (!isLiked) {
            throw new DuplicateException("Еще раз лайкнуть не получится");
        }
        Film film = getFilm(filmId);
        filmStorage.addLike(film, user);
        log.info("Пользователь {} поставил лайк к фильму {}", user.getLogin(), film.getName());
    }

    public void removeLike(int filmId, int userId) {
        User user = getUser(userId);
        boolean isRemoved = new HashSet<>(userStorage.getUsersByFilmId(filmId)).remove(user);
        if (!isRemoved) {
            throw new NotFoundException("Нечего дизлайкать");
        }
        Film film = getFilm(filmId);
        filmStorage.removeLike(film, user);
        log.info("Пользователь {} убрал лайк к фильму {}", user.getLogin(), film.getName());
    }

    public Collection<FilmListDTO> getMostPopular(int count) {
        return filmStorage.popular(count).stream().map(FilmMapper::mapToFilmListDTO).toList();
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
