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
import ru.yandex.practicum.filmorate.request.IdRequest;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public Collection<FilmListDTO> list() {
        return filmStorage.list().stream().map(FilmMapper::mapToFilmListDTO).toList();
    }

    public FilmDTO getById(int id) {
        Film film = getFilm(id);
        List<Genre> genres = genreStorage.getByFilmId(id).stream().toList();
        Mpa mpa = getMpa(film.getMpa().getId());

        return FilmMapper.mapToFilmDTO(film.toBuilder().genres(genres).mpa(mpa).build());
    }

    public FilmDTO create(FilmCreateRequest filmCreateRequest) {
        IdRequest mpaRequest = filmCreateRequest.getMpa();
        Mpa mpa;
        if (mpaRequest != null && mpaRequest.getId() != null) {
            mpa = getMpa(mpaRequest.getId());
        } else {
            mpa = getMpa(1);
        }
        Set<Genre> genres = new HashSet<>();
        for (IdRequest genreRequest : filmCreateRequest.getGenres()) {
            genres.add(getGenre(genreRequest.getId()));
        }

        Film film = Film.builder().name(filmCreateRequest.getName()).description(filmCreateRequest.getDescription())
            .duration(filmCreateRequest.getDuration()).releaseDate(filmCreateRequest.getReleaseDate()).mpa(mpa)
            .build();
        int id = filmStorage.add(film);
        filmStorage.addGenresToFilm(id, genres.stream().toList());
        log.info("Фильм {} добавлен", film.getName());
        return FilmMapper.mapToFilmDTO(getFilm(id));
    }

    public FilmDTO update(FilmUpdateRequest filmRequest) {
        int filmId = filmRequest.getId();
        Film existsFilm = getFilm(filmId);
        IdRequest mpaRequest = filmRequest.getMpa();
        Mpa newMpa;
        if (mpaRequest != null && mpaRequest.getId() != null) {
            newMpa = getMpa(mpaRequest.getId());
        } else {
            newMpa = getMpa(1);
        }
        Set<Genre> newGenres = new HashSet<>();
        for (IdRequest genreRequest : filmRequest.getGenres()) {
            newGenres.add(getGenre(genreRequest.getId()));
        }

        existsFilm = existsFilm.toBuilder().name(filmRequest.getName()).description(filmRequest.getDescription())
            .releaseDate(filmRequest.getReleaseDate()).duration(filmRequest.getDuration()).mpa(newMpa)
            .genres(newGenres.stream().toList()).build();

        filmStorage.update(existsFilm);
        filmStorage.deleteGenresFromFilm(filmId);
        filmStorage.addGenresToFilm(filmId, existsFilm.getGenres());
        log.info("Фильм {} обновлен", filmRequest.getName());
        return FilmMapper.mapToFilmDTO(getFilm(filmId));
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);
        boolean isLiked = film.addLike(user);
        if (!isLiked) {
            throw new DuplicateException("Еще раз лайкнуть не получится");
        }
        filmStorage.addLike(film, user);
        log.info("Пользователь {} поставил лайк к фильму {}", user.getLogin(), film.getName());
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);
        boolean isRemoved = film.removeLike(user);
        if (!isRemoved) {
            throw new NotFoundException("Нечего дизлайкать");
        }
        filmStorage.removeLike(film, user);
        log.info("Пользователь {} убрал лайк к фильму {}", user.getLogin(), film.getName());
    }

    public Collection<FilmListDTO> getMostPopular(int count) {
        return filmStorage.popular(count).stream().map(FilmMapper::mapToFilmListDTO).toList();
    }

    private Film getFilm(int id) {
        Film film = filmStorage.getById(id).orElseThrow(() -> {
            log.error("Фильм id = {} не найден", id);
            return new NotFoundException(String.format("Фильм id = %d не найден", id));
        });
        Collection<Genre> genres = genreStorage.getByFilmId(film.getId());
        Mpa mpa = mpaStorage.getById(film.getMpa().getId()).orElseThrow();
        Set<User> likes = new HashSet<>(userStorage.getUsersByFilmId(id));
        return film.toBuilder().genres(genres.stream().toList()).mpa(mpa).likes(likes).build();
    }

    private User getUser(int id) {
        return userStorage.getById(id).orElseThrow(() -> {
            log.error("Пользователь id = {} не найден", id);
            return new NotFoundException(String.format("Пользователь id = %d не найден", id));
        });
    }

    private Mpa getMpa(int id) {
        return mpaStorage.getById(id).orElseThrow(() -> {
            log.error("mpa id = {} не найден", id);
            return new NotFoundException(String.format("Неверно указано ограничение для фильма, id=%d", id));
        });
    }

    private Genre getGenre(int id) {
        return genreStorage.getById(id).orElseThrow(() -> {
            log.error("genre id = {} не найден", id);
            return new NotFoundException(String.format("Неверно указан жанр фильма, id=%d", id));
        });
    }
}
