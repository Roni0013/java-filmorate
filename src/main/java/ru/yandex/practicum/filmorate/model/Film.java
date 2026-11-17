package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @AfterDate("1895-12-27")
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres;
}
