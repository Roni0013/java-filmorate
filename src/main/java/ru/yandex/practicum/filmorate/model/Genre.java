package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode(of = {"id"})
public class Genre {
    private final int id;
    private final String description;
}
