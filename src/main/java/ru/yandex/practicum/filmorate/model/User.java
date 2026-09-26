package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validator.WithoutSpace;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@Slf4j
public class User {
    private int id;
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
