package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class GenreControllerTest {
    private static final int GENRE_ID = 1;
    private static final String GENRE_NAME = "Комедия";
    private static final int GENRES_COUNT = 6;
    private final GenreController genreController;

    @Test
    @DisplayName("При запросе по id должен вернуть жанр")
    void test_getGenreById_ShouldReturnById() {
        //given && when
        GenreDto genre = genreController.getGenreById(GENRE_ID).getBody();

        //then
        assertNotNull(genre, "В контроллере нет жанров");
        assertEquals(GENRE_NAME, genre.getName(), "В контроллере не верный жанр");
    }

    @Test
    @DisplayName("При запросе должен вернуть все жанры")
    void test_getUsers_ShouldReturnAll() {
        //given && when
        List<GenreDto> genres = genreController.getGenres().getBody();

        //then
        assertNotNull(genres, "В контроллере нет жанров");
        assertEquals(GENRES_COUNT, genres.size(), "В контроллере не верное количество жанров");
    }
}