package ru.yandex.practicum.filmorate.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.WithoutSpace;

import java.time.LocalDate;

@Data
public class UserCreateRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @WithoutSpace
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
