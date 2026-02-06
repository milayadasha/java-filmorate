package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class GenreDbStorageTest {
    private static final int GENRE_ID = 1;
    private static final String GENRE_NAME = "Комедия";
    private static final int GENRES_COUNT = 6;
    private final GenreStorage genreStorage;

    @Test
    @DisplayName("При запросе по id должен вернуть жанр")
    void test_getGenreById_ShouldReturnById() {
        //given && when
        Genre genre = genreStorage.getGenreById(GENRE_ID);

        //then
        assertNotNull(genre, "В контроллере нет жанров");
        assertEquals(GENRE_NAME, genre.getName(), "В контроллере не верный жанр");
    }

    @Test
    @DisplayName("При запросе должен вернуть все жанры")
    void test_getUsers_ShouldReturnAll() {
        //given && when
        List<Genre> genres = genreStorage.getGenres();

        //then
        assertNotNull(genres, "В контроллере нет жанров");
        assertEquals(GENRES_COUNT, genres.size(), "В контроллере не верное количество жанров");
    }
}