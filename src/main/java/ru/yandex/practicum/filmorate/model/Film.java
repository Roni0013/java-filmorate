package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
public class Film {
    @Getter(AccessLevel.NONE)
    private final Set<User> likes = new HashSet<>();
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @AfterDate("1895-12-27")
    private LocalDate releaseDate;
    @Positive
    private int duration;

    public void addLike(User user) {
        likes.add(user);
    }

    public void removeLike(User user) {
        likes.remove(user);
    }

    public int countLikes() {
        return likes.size();
    }

    public static class PopulationComparator implements Comparator<Film> {
        @Override
        public int compare(Film o1, Film o2) {
            return Integer.compare(o1.countLikes(), o2.countLikes());
        }
    }
}
