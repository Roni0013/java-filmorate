package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface UserStorage {
    Collection<User> list();

    int add(User user);

    Optional<User> getById(int id);

    boolean isExists(Predicate<User> predicate);

    void update(User user);
}
