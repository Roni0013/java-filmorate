package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int count = 0;

    @GetMapping
    public Collection<Film> list() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        if (isExistsByName(film)) {
            String message = "Фильм с таким названием уже добавлен";
            log.warn(message);
            throw new DuplicateException(message);
        }
        int nextId = ++count;
        film.setId(nextId);
        films.put(nextId, film);
        log.info("Фильм {} добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        Film existsFilm = films.remove(film.getId());
        if (existsFilm == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (isExistsByName(film)) {
            String message = "Фильм с таким названием уже существует";
            log.warn(message);
            films.put(existsFilm.getId(), existsFilm);
            throw new DuplicateException(message);
        }
        existsFilm = existsFilm.toBuilder().name(film.getName()).description(film.getDescription()).releaseDate(film.getReleaseDate())
            .duration(film.getDuration()).build();
        films.put(existsFilm.getId(), existsFilm);
        log.info("Фильм {} обновлен", film.getName());
        return existsFilm;
    }

    private boolean isExistsByName(Film newFilm) {
        for (Film film : films.values()) {
            if (film.getName().equals(newFilm.getName())) {
                return true;
            }
        }
        return false;
    }
}
