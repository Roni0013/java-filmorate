package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreDTO {
    private final int id;
    private final String name;
}
