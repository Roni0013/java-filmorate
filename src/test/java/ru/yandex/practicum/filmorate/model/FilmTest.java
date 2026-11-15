package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FilmTest {
    private static Validator validator;

    @BeforeAll
    public static void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void createSuccess() {
        String name = "Film";
        String description = "description";
        LocalDate releaseDate = LocalDate.parse("2000-01-01");
        int duration = 95;
        Genre genre1 = new Genre(1, "genre1");
        Genre genre2 = new Genre(1, "genre2");
        Mpa mpa = Mpa.builder().code(Mpa.Code.NC17).build();

        Film film = Film.builder().name(name).description(description).releaseDate(releaseDate).duration(duration)
            .genres(List.of(genre1, genre2)).mpa(mpa).build();

        assertEquals(name, film.getName());
        assertEquals(description, film.getDescription());
        assertEquals(releaseDate, film.getReleaseDate());
        assertEquals(duration, film.getDuration());
        assertEquals(2, film.getGenres().size());
        assertEquals(mpa, film.getMpa());
    }

    @ParameterizedTest
    @CsvSource(value = {
        ", description, 1, 2000-01-01, 95, name",
        "' ', description, 1, 2000-01-01, 95, name",
        "Film, description, 25, 2000-01-01, 95, description",
        "Film, description, 1, 2000-01-01, -95, duration",
        "Film, description, 1, 1895-12-27, 95, releaseDate",
    })
    public void validateErrors(String name, String desc, int repeat, String dateString,
                               int duration, String errorField) {
        String description = desc.repeat(repeat);
        LocalDate releaseDate = LocalDate.parse(dateString);
        Mpa mpa = Mpa.builder().code(Mpa.Code.NC17).build();

        Film film = Film.builder().name(name).description(description).releaseDate(releaseDate).duration(duration)
            .mpa(mpa).build();

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        assertFalse(errors.isEmpty());
        errors.forEach(v -> assertEquals(errorField, v.getPropertyPath().toString()));
    }
}
