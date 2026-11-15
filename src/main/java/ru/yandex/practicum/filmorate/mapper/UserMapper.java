package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDTO mapToUserDTO(User user) {
        return UserDTO.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).login(user.getLogin())
            .birthday(user.getBirthday().toString()).build();
    }
}
