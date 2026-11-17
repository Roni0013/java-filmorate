package ru.yandex.practicum.filmorate.request;

import lombok.Data;

@Data
public class MpaRequest {
    private static final int DEFAULT_MPA = 1;
    private final Integer id;

    public int getIdOrDefault() {
        if (id == null) {
            return DEFAULT_MPA;
        }
        return id;
    }
}
