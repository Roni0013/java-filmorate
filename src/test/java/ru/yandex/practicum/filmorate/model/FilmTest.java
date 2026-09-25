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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


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

        Film film = Film.builder().name(name).description(description).releaseDate(releaseDate).duration(duration)
            .build();

        assertEquals(name, film.getName());
        assertEquals(description, film.getDescription());
        assertEquals(releaseDate, film.getReleaseDate());
        assertEquals(duration, film.getDuration());
    }

    @ParameterizedTest
    @CsvSource(value = {
        ", description, 1, 2000-01-01, 95, name",
        "Film, description, 25, 2000-01-01, 95, description",
        "Film, description, 1, 2000-01-01, -95, duration",
        "Film, description, 1, 1895-12-28, 95, releaseDate",
    })
    public void validateErrors(String name, String desc, int repeat, String dateString,
                               int duration, String errorField) {
        String description = desc.repeat(repeat);
        LocalDate releaseDate = LocalDate.parse(dateString);

        Film film = Film.builder().name(name).description(description).releaseDate(releaseDate).duration(duration).build();

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        errors.forEach(v -> assertEquals(errorField, v.getPropertyPath().toString()));
    }
}
