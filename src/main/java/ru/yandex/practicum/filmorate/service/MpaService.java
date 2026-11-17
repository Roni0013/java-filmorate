package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<MpaDTO> getAll() {
        return mpaStorage.getAll().stream().map(MpaMapper::mapToMpaDTO).toList();
    }

    public MpaDTO getById(int id) {
        return MpaMapper.mapToMpaDTO(mpaStorage.getById(id).orElseThrow(
            () -> {
                log.error("Категория фильма id = {} отсутствует", id);
                return new NotFoundException(String.format("Категория фильма id = %d не найдена", id));
            }
        ));
    }
}
