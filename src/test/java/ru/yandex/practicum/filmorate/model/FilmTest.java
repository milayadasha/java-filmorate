package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {
    private static final int FILM_ID = 1;
    private static final String FILM_NAME = "Фильм";
    private static final String FILM_DESCRIPTION = "Описание фильма";
    private static final int FILM_DURATION = 100;
    private static final LocalDate FILM_RELEASE_DATE = LocalDate.of(1992, Month.DECEMBER, 12);

    @Test
    @DisplayName("Проверяет, что фильма создаётся с переданными параметрами")
    public void test_createFilm_WhenCreateShouldNotEmptyValues() {
        //given && when
        Film film = Film.builder()
                .id(FILM_ID)
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();

        //then
        assertEquals(FILM_ID, film.getId(), "Фильм создался с неверным id");
        assertEquals(FILM_NAME, film.getName(), "Фильм создался с неверным именем");
        assertEquals(FILM_DESCRIPTION, film.getDescription(), "Фильм создался с неверным описанием");
        assertEquals(FILM_RELEASE_DATE, film.getReleaseDate(),
                "Фильм создался с неверной датой релиза");
        assertEquals(FILM_DURATION, film.getDuration(),
                "Фильм создался с неверной длительностью");
    }
}