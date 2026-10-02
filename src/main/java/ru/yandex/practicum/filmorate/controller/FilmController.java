package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

        existsFilm = existsFilm.toBuilder().name(film.getName()).description(film.getDescription()).releaseDate(film.getReleaseDate())
            .duration(film.getDuration()).build();
        films.put(existsFilm.getId(), existsFilm);
        log.info("Фильм {} обновлен", film.getName());
        return existsFilm;
    }

}
