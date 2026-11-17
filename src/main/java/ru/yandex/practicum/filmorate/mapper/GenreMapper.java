package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreMapper {
    public static GenreDTO mapToGenreDTO(Genre genre) {
        return GenreDTO.builder().id(genre.getId()).name(genre.getDescription()).build();
    }
}
