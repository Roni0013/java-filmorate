package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.function.Predicate;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int count = 0;

    @Override
    public Collection<User> list() {
        return List.copyOf(users.values());
    }

    @Override
    public int add(User user) {
        count++;
        user.setId(count);
        users.put(count, user);
        return count;
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean isExists(Predicate<User> predicate) {
        return users.values().stream().anyMatch(predicate);
    }

    @Override
    public void update(User user) {
        if (users.get(user.getId()) == null) {
            throw new NotFoundException("Пользователь для обновления не найден");
        }
        users.put(user.getId(), user);
    }
}
