package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.FilmListDTO;
import ru.yandex.practicum.filmorate.request.FilmCreateRequest;
import ru.yandex.practicum.filmorate.request.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmListDTO> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public FilmDTO item(@PathVariable int id) {
        return filmService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO create(@RequestBody @Valid FilmCreateRequest filmRequest) {
        return filmService.create(filmRequest);
    }

    @PutMapping
    public FilmDTO update(@RequestBody @Valid FilmUpdateRequest filmRequest) {
        return filmService.update(filmRequest);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmListDTO> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("{}", count);
        return filmService.getMostPopular(count);
    }
}
