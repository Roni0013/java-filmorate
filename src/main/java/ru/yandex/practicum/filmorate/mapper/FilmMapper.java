package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.FilmListDTO;
import ru.yandex.practicum.filmorate.model.Film;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static FilmDTO mapToFilmDTO(Film film) {
        return FilmDTO.builder()
            .id(film.getId())
            .name(film.getName())
            .description(film.getDescription())
            .releaseDate(film.getReleaseDate().toString())
            .duration(film.getDuration())
            .mpa(MpaMapper.mapToMpaDTO(film.getMpa()))
            .genres(film.getGenres().stream().map(GenreMapper::mapToGenreDTO).toList())
            .build();
    }

    public static FilmListDTO mapToFilmListDTO(Film film) {
        return FilmListDTO.builder()
            .id(film.getId())
            .name(film.getName())
            .description(film.getDescription())
            .releaseDate(film.getReleaseDate().toString())
            .duration(film.getDuration())
            .build();
    }
}
