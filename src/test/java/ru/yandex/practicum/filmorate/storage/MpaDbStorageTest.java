package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({MpaDbStorage.class, MpaRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    public void testList() {
        Collection<Mpa> mpaList = mpaStorage.list();

        assertEquals(5, mpaList.size());
    }

    @Test
    public void testGetById() {
        Mpa mpa = mpaStorage.getById(1).orElseThrow();

        assertEquals(1, mpa.getId());
        assertEquals(Mpa.Code.G, mpa.getCode());
        assertEquals("Нет возрастных ограничений", mpa.getDescription());
    }
}
