package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int count = 0;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (isLoginEmailExists(user)) {
            String message = "Пользователь с указанными login или email уже существует";
            log.warn(message);
            throw new DuplicateException(message);
        }
        int nextId = ++count;
        user.setId(nextId);
        users.put(nextId, user);
        log.info("Пользователь {} добавлен", user.getLogin());
        return user;
    }

    @GetMapping
    public Collection<User> list() {
        return users.values();
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User existsUser = users.remove(user.getId());
        if (existsUser == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (isLoginEmailExists(user)) {
            String message = "Пользователь с указанными login или email уже существует";
            log.warn(message);
            users.put(existsUser.getId(), existsUser);
            throw new DuplicateException(message);
        }
        existsUser.toBuilder().name(user.getName()).email(user.getEmail()).birthday(user.getBirthday())
            .login(user.getLogin()).build();
        existsUser.setName(user.getName());
        existsUser.setEmail(user.getEmail());
        existsUser.setBirthday(user.getBirthday());
        existsUser.setLogin(user.getLogin());
        users.put(existsUser.getId(), existsUser);
        log.info("Пользователь {} обновлен", existsUser.getLogin());
        return existsUser;
    }

    private boolean isLoginEmailExists(User newUser) {
        for (User user : users.values()) {
            if (user.getLogin().equals(newUser.getLogin()) || user.getEmail().equals(newUser.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
