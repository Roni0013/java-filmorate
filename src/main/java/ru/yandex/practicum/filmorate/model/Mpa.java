package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
public class Mpa {
    private final int id;
    private final Code code;
    private final String description;

    @Getter
    public enum Code {
        G("G"),
        PG("PG"),
        PG13("PG-13"),
        R("R"),
        NC17("NC-17");

        private final String label;

        Code(String label) {
            this.label = label;
        }
    }
}
