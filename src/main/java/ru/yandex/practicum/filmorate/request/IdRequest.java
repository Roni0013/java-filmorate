package ru.yandex.practicum.filmorate.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdRequest {
    @NotNull
    private Integer id;
}
