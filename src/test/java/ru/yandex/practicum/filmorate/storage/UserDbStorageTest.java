package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({UserDbStorage.class, UserRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    public void setUp() {
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", 1000, "testUser", "Иванов Иван", "test@test.tets", "2026-11-11");
    }

    @Test
    public void testGetAll() {
        Collection<User> users = userStorage.getAll();

        assertEquals(1, users.size());
    }

    @Test
    public void testGetById() {
        User user = userStorage.getById(1000).orElseThrow();

        assertEquals(1000, user.getId());
        assertEquals("testUser", user.getLogin());
        assertEquals("Иванов Иван", user.getName());
        assertEquals("test@test.tets", user.getEmail());
        assertEquals("2026-11-11", user.getBirthday().toString());
    }

    @Test
    public void testAdd() {
        String login = "login";
        String name = "Петр Петров";
        String email = "tmp@test.test";
        LocalDate birthday = LocalDate.parse("2020-12-12");
        User newUser = User.builder().login(login).name(name).email(email).birthday(birthday).build();
        int id = userStorage.add(newUser);

        User addedUser = userStorage.getById(id).orElseThrow();

        assertEquals(login, addedUser.getLogin());
        assertEquals(name, addedUser.getName());
        assertEquals(email, addedUser.getEmail());
        assertEquals(birthday, addedUser.getBirthday());
    }

    @Test
    public void testUpdate() {
        String newLogin = "newLogin";
        String newName = "Василий Васильев";
        String newEmail = "new@mail.tets";
        LocalDate newBirthday = LocalDate.parse("1990-02-02");
        User toUpdateUser = User.builder().id(1000).login(newLogin).name(newName).email(newEmail).birthday(newBirthday)
            .build();

        userStorage.update(toUpdateUser);

        User updatedUser = userStorage.getById(1000).orElseThrow();
        assertEquals(newLogin, updatedUser.getLogin());
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(newName, updatedUser.getName());
        assertEquals(newBirthday, updatedUser.getBirthday());
    }

    @Test
    public void testAddFriend() {
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", 1010, "friendUser", "Петров Петр", "friend@test.tets", "2026-11-11");
        User user = User.builder().id(1000).build();
        User friend = User.builder().id(1010).build();

        userStorage.addFriend(user, friend);

        Collection<User> friends = userStorage.getFriendsByUserId(1000);
        assertEquals(1, friends.size());
    }

    @Test
    public void testRemoveFriend() {
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", 1010, "friendUser", "Петров Петр", "friend@test.tets", "2026-11-11");
        jdbc.update("INSERT INTO user_link(user_id_from,user_id_to) "
            + "VALUES (?,?)", 1000, 1010);
        User user = User.builder().id(1000).build();
        User friend = User.builder().id(1010).build();

        userStorage.deleteFriend(user, friend);

        Collection<User> friends = userStorage.getFriendsByUserId(1000);
        assertTrue(friends.isEmpty());
    }

    @Test
    public void testCommonFriends() {
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", 1010, "friendUser", "Петров Петр", "friend@test.tets", "2026-11-11");
        jdbc.update("INSERT INTO users(id,login,name,email,birthday) "
            + "VALUES (?,?,?,?,?)", 1020, "commonFriend", "Сидоров Сидоров", "common_friend@test.test", "2020-11-11");
        jdbc.update("INSERT INTO user_link(user_id_from,user_id_to) "
            + "VALUES (?,?)", 1000, 1020);
        jdbc.update("INSERT INTO user_link(user_id_from,user_id_to) "
            + "VALUES (?,?)", 1010, 1020);

        Collection<User> commonFriends = userStorage.getCommonFriends(1000, 1010);

        assertEquals(1, commonFriends.size());
        assertEquals(1020, commonFriends.stream().findFirst().orElseThrow().getId());
    }
}
