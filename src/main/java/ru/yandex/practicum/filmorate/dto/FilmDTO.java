package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FilmDTO {
    private final int id;
    private final String name;
    private final String description;
    private final String releaseDate;
    private final int duration;
    private final MpaDTO mpa;
    private final List<GenreDTO> genres;
}
