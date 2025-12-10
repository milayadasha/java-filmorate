package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private static final String FILM_NAME = "Фильм";
    private static final String FILM_NAME_2 = "Второй фильм";

    private static final String FILM_DESCRIPTION = "Описание фильма";
    private static final String FILM_DESCRIPTION_2 = "Описание второго фильма";
    private static final String FILM_DESCRIPTION_INCORRECT = "Очень " + "очень".repeat(199) + "длинное описание";

    private static final LocalDate FILM_RELEASE_DATE = LocalDate.of(1992, Month.DECEMBER, 12);
    private static final LocalDate FILM_RELEASE_DATE_2 = LocalDate.of(2005, Month.APRIL, 28);
    private static final LocalDate FILM_RELEASE_DATE_INCORRECT = LocalDate.of(1600, Month.DECEMBER, 5);

    private static final int FILM_DURATION = 100;
    private static final int FILM_DURATION_2 = 200;
    private static final int FILM_DURATION_INCORRECT = -100;

    FilmController filmController;

    @BeforeEach
    @DisplayName("Инициализирует контроллер")
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("При добавлении двух фильмов контроллер должен возвращать их")
    void test_getFilms_WhenAddedFilms_ShouldReturnAll() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        Film film2 = Film.builder().name(FILM_NAME_2).description(FILM_DESCRIPTION_2).releaseDate(FILM_RELEASE_DATE_2)
                .duration(FILM_DURATION_2).build();

        //when
        filmController.addFilm(film);
        filmController.addFilm(film2);
        List<Film> filmsByController = filmController.getFilms().stream().toList();

        //then
        assertEquals(2, filmsByController.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME, filmsByController.get(0).getName(), "В контроллере не корректный 1-ый фильм");
        assertEquals(FILM_NAME_2, filmsByController.get(1).getName(), "В контроллере не корректный 2-ой фильм");
    }

    @Test
    @DisplayName("При добавлении фильма с корректными полями контроллер должен добавить его")
    void test_addFilm_WhenCorrectFields_ShouldAddToController() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();

        //when
        filmController.addFilm(film);
        List<Film> filmsByController = filmController.getFilms().stream().toList();

        //then
        assertEquals(1, filmsByController.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME, filmsByController.get(0).getName(), "В контроллере некорректный фильм");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректным описанием контроллер должен выбросить ошибку")
    void test_addFilm_WhenIncorrectDescription_ShouldThrowsError() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION_INCORRECT)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.addFilm(film),
                "Фильм может быть добавлен с некорректным описанием");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректной длительностью контроллер должен выбросить ошибку")
    void test_addFilm_WhenIncorrectDuration_ShouldThrowsError() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION_INCORRECT).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.addFilm(film),
                "Фильм может быть добавлен с некорректной длительностью");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректной датой релиза контроллер должен выбросить ошибку")
    void test_addFilm_WhenIncorrectReleaseDate_ShouldThrowsError() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE_INCORRECT)
                .duration(FILM_DURATION).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.addFilm(film),
                "Фильм может быть добавлен с некорректной датой релиза");
    }

    @Test
    @DisplayName("При обновлении фильма с корректными данными контроллер должен обновить его")
    void test_updateFilm_WhenCorrectFields_ShouldUpdateInController() {
        //given
        Film film = filmController.addFilm(Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build());

        //when
        Film updatedFilm = film.toBuilder().name(FILM_NAME_2).build();
        filmController.updateFilm(updatedFilm);
        List<Film> filmsByController = filmController.getFilms().stream().toList();

        //then
        assertEquals(1, filmsByController.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME_2, filmsByController.get(0).getName(), "В контроллере некорректный фильм");
    }

    @Test
    @DisplayName("При обновлении фильма с некорректным описанием контроллер должен выбросить ошибку")
    void test_updateFilm_WhenIncorrectDescription_ShouldThrowsError() {
        //given
        Film film = filmController.addFilm(Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build());

        //when
        Film updatedFilm = film.toBuilder().description(FILM_DESCRIPTION_INCORRECT).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректным описанием");
    }

    @Test
    @DisplayName("При обновлении фильма с некорректной длительностью контроллер должен выбросить ошибку")
    void test_updateFilm_WhenIncorrectDuration_ShouldThrowsError() {
        //given
        Film film = filmController.addFilm(Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build());

        //when
        Film updatedFilm = film.toBuilder().duration(FILM_DURATION_INCORRECT).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректной длительностью");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректной датой релиза контроллер должен выбросить ошибку")
    void test_updateFilm_WhenIncorrectReleaseDate_ShouldThrowsError() {
        //given
        Film film = filmController.addFilm(Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build());

        //when
        Film updatedFilm = film.toBuilder().releaseDate(FILM_RELEASE_DATE_INCORRECT).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректной датой релиза");
    }

}