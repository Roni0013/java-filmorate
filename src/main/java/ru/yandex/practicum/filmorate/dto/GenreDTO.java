package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreDTO implements Comparable<GenreDTO> {
    private final int id;
    private final String name;

    @Override
    public int compareTo(GenreDTO o) {
        return Integer.compare(id, o.getId());
    }
}
