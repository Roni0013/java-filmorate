package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private final int id;
    private final String login;
    private final String name;
    private final String email;
    private final String birthday;
}
