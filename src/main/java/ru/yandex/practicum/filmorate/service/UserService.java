package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> list() {
        return userStorage.list();
    }

    public User create(User user) {
        log.trace("Добавление пользователя {}", user);
        checkExistsUserByLoginAndEmail(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        int id = userStorage.add(user);
        log.info("Пользователь {} добавлен", user.getLogin());
        return userStorage.getById(id).orElseThrow();
    }

    public User update(User user) {
        User existsUser = getUser(user.getId());
        checkExistsUserByLoginAndEmail(user);
        existsUser = existsUser.toBuilder().name(user.getName()).email(user.getEmail())
            .login(user.getLogin()).birthday(user.getBirthday()).build();
        userStorage.update(existsUser);
        log.info("Пользователь {} обновлен", user.getLogin());
        return existsUser;
    }

    public User getById(int id) {
        return getUser(id);
    }

    public User addFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.addFriend(friend);
        friend.addFriend(user);
        log.info("Пользователь {} и {} теперь друзья", user.getLogin(), friend.getLogin());
        return friend;
    }

    public User removeFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.removeFriend(friend);
        friend.removeFriend(user);
        log.info("Пользователь {} и {} теперь не друзья", user.getLogin(), friend.getLogin());
        return friend;
    }

    public Collection<User> listFriends(int id) {
        return getUser(id).listFriends();
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        User user = getUser(id);
        User otherUser = getUser(otherId);
        Set<User> otherFriends = otherUser.listFriends();
        return user.listFriends().stream().filter(otherFriends::contains).toList();
    }

    private User getUser(int id) {
        return userStorage.getById(id).orElseThrow(() -> {
            log.error("Пользователь id = {} не найден", id);
            return new NotFoundException(String.format("Пользователь id = %d не найден", id));
        });
    }

    private void checkExistsUserByLoginAndEmail(User user) {
        if (userStorage.isExists(u -> {
            if (user.getId() != null && u.getId().equals(user.getId())) {
                return false;
            }
            return u.getLogin().equals(user.getLogin()) || u.getEmail().equals(user.getEmail());
        })
        ) {
            String errorMessage = "Пользователь с указанными login или email уже существует";
            log.error(errorMessage);
            throw new DuplicateException(errorMessage);
        }
    }
}
