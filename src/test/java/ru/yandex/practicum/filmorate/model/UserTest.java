package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private static Validator validator;

    @BeforeAll
    public static void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
        "Name, Name",
        ", loginUser"
    })
    public void createSuccess(String name, String expectedName) {
        int id = 5;
        String login = "loginUser";
        String email = "test@test.test";
        LocalDate birthday = LocalDate.parse("2020-01-01");

        User user = new User.UserBuilder().id(id).login(login).name(name).email(email).birthday(birthday).build();

        assertEquals(login, user.getLogin());
        assertEquals(email, user.getEmail());
        assertEquals(expectedName, user.getName());
        assertEquals(birthday, user.getBirthday());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "login User, test@test.test, 2000-01-01, login",
        "loginUser, testTest.test, 2000-01-01, email",
        "loginUser, test@Test.test, 3000-01-01, birthday",
        ", test@test.test, 2000-01-01, login",
        "loginUser, , 2000-01-01, email",
    })
    public void validateErrors(String login, String email, String birthdayString, String errorField) {
        int id = 5;
        String name = "Name";
        User user = new User.UserBuilder().id(id).login(login).name(name).email(email)
            .birthday(LocalDate.parse(birthdayString)).build();

        Set<ConstraintViolation<User>> errors = validator.validate(user);

        errors.forEach(v -> assertEquals(errorField, v.getPropertyPath().toString()));
    }
}
