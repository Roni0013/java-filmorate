package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @AfterDate("1895-12-27")
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
