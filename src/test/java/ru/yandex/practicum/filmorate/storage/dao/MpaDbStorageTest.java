package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class MpaDbStorageTest {
    private static final int MPA_ID = 1;
    private static final String MPA_NAME = "G";
    private static final int MPA_COUNT = 5;
    private final MpaStorage mpaStorage;

    @Test
    @DisplayName("При запросе по id должен вернуть жанр")
    void test_getMpaById_ShouldReturnById() {
        //given && when
        Mpa mpa = mpaStorage.getMpaById(MPA_ID);

        //then
        assertNotNull(mpa, "В контроллере нет рейтингом");
        assertEquals(MPA_NAME, mpa.getName(), "В контроллере не верный рейтинг");
    }

    @Test
    @DisplayName("При запросе должен вернуть все жанры")
    void test_getMpaList_ShouldReturnAll() {
        //given && when
        List<Mpa> mpaList = mpaStorage.getMpaList();

        //then
        assertNotNull(mpaList, "В контроллере нет рейтингов");
        assertEquals(MPA_COUNT, mpaList.size(), "В контроллере не верное количество рейтингов");
    }
}