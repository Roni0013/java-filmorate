package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.WithoutSpace;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
public class User {
    @Getter(AccessLevel.NONE)
    private final Set<User> friends = new HashSet<>();
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

    public void addFriend(User user) {
        friends.add(user);
    }

    public void removeFriend(User user) {
        friends.remove(user);
    }

    public Set<User> listFriends() {
        return Set.copyOf(friends);
    }
}
