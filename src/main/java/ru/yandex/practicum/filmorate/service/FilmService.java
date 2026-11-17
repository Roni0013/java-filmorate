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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private static final int DEFAULT_MPA = 1;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public Collection<FilmListDTO> list() {
        return filmStorage.list().stream().map(FilmMapper::mapToFilmListDTO).toList();
    }

    public FilmDTO getById(int id) {
        return FilmMapper.mapToFilmDTO(getFilm(id));
    }

    public FilmDTO create(FilmCreateRequest filmRequest) {
        Mpa mpa = getMpaOrDefault(filmRequest.getMpa());
        Set<Genre> genres = new HashSet<>();
        filmRequest.getGenres().forEach(idRequest -> genres.add(getGenre(idRequest.getId())));

        Film film = Film.builder().name(filmRequest.getName()).description(filmRequest.getDescription())
            .duration(filmRequest.getDuration()).releaseDate(filmRequest.getReleaseDate())
            .mpa(mpa).build();
        int id = filmStorage.add(film);
        filmStorage.addGenresToFilm(id, genres.stream().toList());
        log.info("Фильм {} добавлен", film.getName());
        return FilmMapper.mapToFilmDTO(getFilm(id));
    }

    public FilmDTO update(FilmUpdateRequest filmRequest) {
        int filmId = filmRequest.getId();
        Film existsFilm = getFilm(filmId);
        Mpa newMpa = getMpaOrDefault(filmRequest.getMpa());
        Set<Genre> newGenres = new HashSet<>();
        filmRequest.getGenres().forEach(idRequest -> newGenres.add(getGenre(idRequest.getId())));

        existsFilm = existsFilm.toBuilder().name(filmRequest.getName()).description(filmRequest.getDescription())
            .releaseDate(filmRequest.getReleaseDate()).duration(filmRequest.getDuration())
            .mpa(newMpa)
            .genres(newGenres).build();

        filmStorage.update(existsFilm);
        filmStorage.deleteGenresFromFilm(filmId);
        filmStorage.addGenresToFilm(filmId, existsFilm.getGenres().stream().toList());
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

    private Mpa getMpa(int id) {
        return mpaStorage.getById(id).orElseThrow(() -> {
            log.error("mpa id = {} не найден", id);
            return new NotFoundException(String.format("Неверно указано ограничение для фильма, id=%d", id));
        });
    }

    private Mpa getMpaOrDefault(IdRequest mpaRequest) {
        if (mpaRequest != null && mpaRequest.getId() != null) {
            return getMpa(mpaRequest.getId());
        } else {
            return getMpa(DEFAULT_MPA);
        }
    }

    private Genre getGenre(int id) {
        return genreStorage.getById(id).orElseThrow(() -> {
            log.error("genre id = {} не найден", id);
            return new NotFoundException(String.format("Неверно указан жанр фильма, id=%d", id));
        });
    }
}
