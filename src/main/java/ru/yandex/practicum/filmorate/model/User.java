package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.WithoutSpace;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@ToString
public class User {
    @Getter(AccessLevel.NONE)
    private Set<User> friends;
    private Integer id;
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

    public boolean addFriend(User user) {
        return friends.add(user);
    }

    public boolean removeFriend(User user) {
        return friends.remove(user);
    }

    public Set<User> listFriends() {
        return Set.copyOf(friends);
    }
}
