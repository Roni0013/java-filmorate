package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.request.UserCreateRequest;
import ru.yandex.practicum.filmorate.request.UserUpdateRequest;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Collection<UserDTO> getAll() {
        return userStorage.getAll().stream().map(UserMapper::mapToUserDTO).toList();
    }

    public UserDTO create(UserCreateRequest userRequest) {
        User user = User.builder().login(userRequest.getLogin()).name(userRequest.getName())
            .email(userRequest.getEmail()).birthday(userRequest.getBirthday()).build();
        log.debug("Добавление пользователя {}", user);
        int id = userStorage.add(user);
        log.info("Пользователь {} добавлен", user.getLogin());
        return UserMapper.mapToUserDTO(userStorage.getById(id).orElseThrow());
    }

    public UserDTO update(UserUpdateRequest updateRequest) {
        User existsUser = getUser(updateRequest.getId());
        checkExistsUserByLoginAndEmail(existsUser);
        existsUser = existsUser.toBuilder().name(updateRequest.getName()).email(updateRequest.getEmail())
            .login(updateRequest.getLogin()).birthday(updateRequest.getBirthday()).build();
        userStorage.update(existsUser);
        log.info("Пользователь {} обновлен", existsUser.getLogin());
        return UserMapper.mapToUserDTO(existsUser);
    }

    public UserDTO getById(int id) {
        return UserMapper.mapToUserDTO(getUser(id));
    }

    public void addFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        Set<User> existsFriends = new HashSet<>(userStorage.getFriendsByUserId(userId));
        user = user.toBuilder().friends(existsFriends).build();

        boolean isAdded = user.addFriend(friend);
        if (!isAdded) {
            log.warn("Попытка добавить у пользователя id={} существующего друга id={}", userId, friendId);
            throw new DuplicateException("Уже друзья");
        }
        userStorage.addFriend(user, friend);
        log.info("Пользователь {} и {} теперь друзья", user.getLogin(), friend.getLogin());
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUser(userId);
        Set<User> existsFriends = new HashSet<>(userStorage.getFriendsByUserId(userId));
        user = user.toBuilder().friends(existsFriends).build();
        User friend = getUser(friendId);
        user.removeFriend(friend);
        userStorage.deleteFriend(user, friend);
        log.info("Пользователь {} и {} теперь не друзья", user.getLogin(), friend.getLogin());
    }

    public Collection<UserDTO> getFriends(int id) {
        User user = getUser(id);
        Collection<User> friends = userStorage.getFriendsByUserId(user.getId());
        log.debug("Найдено {} друзей", friends.size());
        return friends.stream().map(UserMapper::mapToUserDTO).toList();
    }

    public Collection<UserDTO> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriends(id, otherId).stream().map(UserMapper::mapToUserDTO).toList();
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
