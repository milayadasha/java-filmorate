package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class MpaControllerTest {
    private static final int MPA_ID = 1;
    private static final String MPA_NAME = "G";
    private static final int MPA_COUNT = 5;
    private final MpaController mpaController;

    @Test
    @DisplayName("При запросе по id должен вернуть рейтинг")
    void test_getMpaById_ShouldReturnById() {
        //given && when
        MpaDto mpa = mpaController.getMpaById(MPA_ID).getBody();

        //then
        assertNotNull(mpa, "В контроллере нет рейтингом");
        assertEquals(MPA_NAME, mpa.getName(), "В контроллере не верный рейтинг");
    }

    @Test
    @DisplayName("При запросе должен вернуть все рейтинги")
    void test_getMpaList_ShouldReturnAll() {
        //given && when
        List<MpaDto> mpaList = mpaController.getMpaList().getBody();

        //then
        assertNotNull(mpaList, "В контроллере нет рейтингов");
        assertEquals(MPA_COUNT, mpaList.size(), "В контроллере не верное количество рейтингов");
    }
}