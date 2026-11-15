package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<GenreDTO> list() {
        return genreStorage.list().stream().map(GenreMapper::mapToGenreDTO).toList();
    }

    public GenreDTO getById(int id) {
        return GenreMapper.mapToGenreDTO(genreStorage.getById(id).orElseThrow(
            () -> {
                log.error("Жанр id = {} не найден", id);
                return new NotFoundException(String.format("Жанр фильма id = %d не найден", id));
            }
        ));
    }
}
