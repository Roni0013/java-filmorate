package ru.yandex.practicum.filmorate.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class FilmCreateRequest {
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @NotNull
    @AfterDate("1895-12-27")
    private final LocalDate releaseDate;
    @Positive
    private final Integer duration;
    @NotNull
    private final MpaRequest mpa;
    private final List<GenreRequest> genres = new ArrayList<>();
}
