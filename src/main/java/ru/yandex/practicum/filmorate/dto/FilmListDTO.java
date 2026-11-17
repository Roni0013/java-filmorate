package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmListDTO {
    private final int id;
    private final String name;
    private final String description;
    private final String releaseDate;
    private final int duration;
}
