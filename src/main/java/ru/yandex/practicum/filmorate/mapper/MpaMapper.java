package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MpaMapper {
    public static MpaDTO mapToMpaDTO(Mpa mpa) {
        return MpaDTO.builder().id(mpa.getId()).name(mpa.getCode().getLabel()).description(mpa.getDescription())
            .build();
    }
}
