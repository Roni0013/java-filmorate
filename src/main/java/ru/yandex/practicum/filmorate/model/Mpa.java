package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
public class Mpa {
    private final int id;
    private final MpaCode code;
}
