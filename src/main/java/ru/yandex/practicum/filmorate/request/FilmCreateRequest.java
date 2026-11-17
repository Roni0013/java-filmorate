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
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @AfterDate("1895-12-27")
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    @NotNull
    private IdRequest mpa;
    private List<IdRequest> genres = new ArrayList<>();
}
