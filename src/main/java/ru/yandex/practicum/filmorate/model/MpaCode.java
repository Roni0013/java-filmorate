package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

@Getter
public enum MpaCode {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String label;

    MpaCode(String label) {
        this.label = label;
    }

    public static MpaCode getCodeByString(String dbCode) {
        return switch (dbCode) {
            case "G" -> MpaCode.G;
            case "PG" -> MpaCode.PG;
            case "PG-13" -> MpaCode.PG13;
            case "R" -> MpaCode.R;
            case "NC-17" -> MpaCode.NC17;
            default ->
                throw new InternalServerException(String.format("Ошибка данных в категориях фильмов %s", dbCode));
        };
    }
}
