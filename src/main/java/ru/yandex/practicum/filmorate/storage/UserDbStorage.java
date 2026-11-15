package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String ALL_QUERY = "SELECT id,login,name,email,birthday FROM users";
    private static final String BY_ID_QUERY = "SELECT id,login,name,email,birthday FROM users WHERE id=?";
    private static final String INSERT_QUERY = "INSERT INTO users(login,name,email,birthday) VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE users SET login=?,name=?,email=?,birthday=? WHERE id=?";
    private static final String INSERT_LINK_QUERY = "INSERT INTO user_link(user_id_from, user_id_to) VALUES(?,?)";
    private static final String DELETE_LINK_QUERY = "DELETE FROM user_link WHERE user_id_from=? AND user_id_to=?";
    private static final String FRIENDS_QUERY =
        "SELECT u.id,u.login,u.name,u.email,u.birthday FROM users u JOIN user_link ul ON ul.user_id_to=u.id "
            + "WHERE ul.user_id_from=?";
    private static final String LIKES_QUERY =
        "SELECT u.id,u.login,u.name,u.email,u.birthday FROM users u JOIN likes l ON l.user_id=u.id WHERE l.film_id=?";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> list() {
        return findMany(ALL_QUERY);
    }

    @Override
    public int add(User user) {
        return insert(INSERT_QUERY, user.getLogin(), user.getName(), user.getEmail(),
            Date.valueOf(user.getBirthday()));
    }

    @Override
    public Optional<User> getById(int id) {
        return findOne(BY_ID_QUERY, id);
    }

    @Override
    public boolean isExists(Predicate<User> predicate) {
        return list().stream().anyMatch(predicate);
    }

    @Override
    public void update(User user) {
        int result = update(UPDATE_QUERY, user.getLogin(), user.getName(), user.getEmail(),
            Date.valueOf(user.getBirthday()), user.getId());
        if (result == 0) {
            throw new NotFoundException(String.format("Ошибка обновления пользователя, id=%d", user.getId()));
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        insert(INSERT_LINK_QUERY, user.getId(), friend.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        update(DELETE_LINK_QUERY, user.getId(), friend.getId());
    }

    @Override
    public Collection<User> getFriendsByUserId(int id) {
        return findMany(FRIENDS_QUERY, id);
    }

    @Override
    public Collection<User> getUsersByFilmId(int id) {
        return findMany(LIKES_QUERY, id);
    }
}
