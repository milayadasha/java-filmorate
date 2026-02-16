package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
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

    private static final int FILM_ID = 100;
    private static final int FILM_COUNT_1 = 1;
    private static final int FILM_DEFAULT_COUNT = 10;

    private static final String USER_NAME = "Вася";
    private static final String USER_EMAIL = "mail@mail.ru";
    private static final String USER_EMAIL_2 = "mail2@mail.ru";
    private static final String USER_LOGIN = "user";
    private static final String USER_LOGIN_2 = "user2";
    private static final LocalDate USER_BIRTHDAY = LocalDate.of(1992, Month.DECEMBER, 12);
    private static final Integer USER_ID = 567;

    private static final String DELETE_FILMS = "DELETE FROM films;";
    private static final String DELETE_USERS = "DELETE FROM users;";

    private final FilmController filmController;
    private final UserController userController;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    @DisplayName("Чистим БД")
    void cleanDatabase() {
        jdbcTemplate.update(DELETE_FILMS);
        jdbcTemplate.update(DELETE_USERS);
    }

    @Test
    @DisplayName("При добавлении двух фильмов контроллер должен возвращать их")
    void test_getFilms_WhenAddedFilms_ShouldReturnAll() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        NewFilmRequest film2 = NewFilmRequest.builder().name(FILM_NAME_2).description(FILM_DESCRIPTION_2)
                .releaseDate(FILM_RELEASE_DATE_2)
                .duration(FILM_DURATION_2).build();

        //when
        filmController.addFilm(film);
        filmController.addFilm(film2);
        List<FilmDto> filmsByController = filmController.getFilms().getBody();

        //then
        assertNotNull(filmsByController, "В контроллере нет фильмов");
        assertEquals(2, filmsByController.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME, filmsByController.get(0).getName(), "В контроллере не корректный 1-ый фильм");
        assertEquals(FILM_NAME_2, filmsByController.get(1).getName(), "В контроллере не корректный 2-ой фильм");
    }

    @Test
    @DisplayName("При добавлении фильма с корректными полями контроллер должен добавить его")
    void test_addFilm_WhenCorrectFields_ShouldAddToController() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();

        //when
        filmController.addFilm(film);
        List<FilmDto> filmsByController = filmController.getFilms().getBody();

        //then
        assertNotNull(filmsByController, "В контроллере нет фильмов");
        assertEquals(1, filmsByController.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME, filmsByController.get(0).getName(), "В контроллере некорректный фильм");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректным описанием контроллер должен выбросить ошибку")
    void test_addFilm_WhenIncorrectDescription_ShouldThrowsError() {
        //given && when
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION_INCORRECT)
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
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
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
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
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
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        //when
        UpdateFilmRequest updatedFilm = UpdateFilmRequest.builder().id(filmId).name(FILM_NAME_2)
                .description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        filmController.updateFilm(updatedFilm);
        List<FilmDto> filmsByController = filmController.getFilms().getBody();

        //then
        assertNotNull(filmsByController, "В контроллере нет фильмов");
        assertEquals(1, filmsByController.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME_2, filmsByController.get(0).getName(), "В контроллере некорректный фильм");
    }

    @Test
    @DisplayName("При обновлении фильма с некорректным описанием контроллер должен выбросить ошибку")
    void test_updateFilm_WhenIncorrectDescription_ShouldThrowsError() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        //when
        UpdateFilmRequest updatedFilm = UpdateFilmRequest.builder().id(filmId).name(FILM_NAME_2)
                .description(FILM_DESCRIPTION_INCORRECT)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректным описанием");
    }

    @Test
    @DisplayName("При обновлении фильма с некорректной длительностью контроллер должен выбросить ошибку")
    void test_updateFilm_WhenIncorrectDuration_ShouldThrowsError() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        //when
        UpdateFilmRequest updatedFilm = UpdateFilmRequest.builder().id(filmId).name(FILM_NAME_2)
                .description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION_INCORRECT).build();
        //then
        assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректной длительностью");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректной датой релиза контроллер должен выбросить ошибку")
    void test_updateFilm_WhenIncorrectReleaseDate_ShouldThrowsError() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        //when
        UpdateFilmRequest updatedFilm = UpdateFilmRequest.builder().id(filmId).name(FILM_NAME_2)
                .description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE_INCORRECT)
                .duration(FILM_DURATION).build();

        //then
        assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректной датой релиза");
    }

    @Test
    @DisplayName("При получении существующего фильма по id контроллер должен вернуть его")
    void test_getFilmById_WhenFilmExists_ShouldReturnOne() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        //when
        FilmDto findFilm = filmController.getFilmById(filmId).getBody();

        //then
        assertNotNull(findFilm, "В контроллере нет фильмов");
        assertEquals(FILM_NAME, findFilm.getName(), "В контроллере некорректный фильм");
    }

    @Test
    @DisplayName("При получении несуществующего фильма по id контроллер должен вернуть ошибку")
    void test_getFilmById_WhenFilmNotExists_ShouldThrowsError() {
        //given && when && then
        assertThrows(NotFoundException.class,
                () -> filmController.getFilmById(FILM_ID),
                "Из контроллера получен несуществующий фильм");
    }

    @Test
    @DisplayName("При добавлении лайка существующему фильму от существующего пользователя должен вернуться OK 200")
    void test_addLikeByUser_WhenFilmAndUserExist_ShouldReturn200() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();


        //when
        ResponseEntity<Void> response = filmController.addLikeByUser(filmId, userId);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Вернулся не успешный ответ");
    }

    @Test
    @DisplayName("При добавлении лайка существующему фильму от несуществующего пользователя должен вернуть ошибку")
    void test_addLikeByUser_WhenUserNotExists_ShouldThrowsError() {
        //given && when
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        //then
        assertThrows(NotFoundException.class, () -> filmController.addLikeByUser(filmId, USER_ID),
                "Удалось поставить лайк от несуществующего пользователя");
    }

    @Test
    @DisplayName("При добавлении лайка несуществующему фильму от существующего пользователя должен вернуть ошибку")
    void test_addLikeByUser_WhenFilmNotExists_ShouldThrowsError() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //then
        assertThrows(NotFoundException.class, () -> filmController.addLikeByUser(FILM_ID, userId),
                "Удалось поставить лайк несуществующему фильму");
    }

    @Test
    @DisplayName("При удалении лайка с существующего фильма от существующего пользователя должен вернуть OK 200")
    void test_removeLikeByUser_WhenFilmAndUserExist_ShouldReturn200() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //when
        filmController.addLikeByUser(filmId, userId);
        ResponseEntity<Void> response = filmController.removeLikeByUser(filmId, userId);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Вернулся не успешный ответ");
    }

    @Test
    @DisplayName("При удалении лайка с существующего фильма от несуществующего пользователя должен вернуть ошибку")
    void test_removeLikeByUser_WhenUserNotExists_ShouldThrowsError() {
        //given && when
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();

        //then
        assertThrows(NotFoundException.class, () -> filmController.removeLikeByUser(filmId, USER_ID),
                "Удалось убрать лайк от несуществующего пользователя");
    }

    @Test
    @DisplayName("При удалении лайка с несуществующего фильма от существующего пользователя должен вернуть ошибку")
    void test_removeLikeByUser_WhenFilmNotExists_ShouldThrowsError() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //then
        assertThrows(NotFoundException.class, () -> filmController.removeLikeByUser(FILM_ID, userId),
                "Удалось убрать лайк с несуществующего фильма");
    }

    @Test
    @DisplayName("При запросе одного фильма должен вернуть самый популярный")
    void test_getMostPopularFilms_WhenRequestOne_ShouldReturnOne() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        NewFilmRequest film2 = NewFilmRequest.builder().name(FILM_NAME_2).description(FILM_DESCRIPTION_2)
                .releaseDate(FILM_RELEASE_DATE_2)
                .duration(FILM_DURATION_2).build();

        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();
        int filmId2 = Objects.requireNonNull(filmController.addFilm(film2).getBody()).getId();

        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY).build();
        int userId2 = Objects.requireNonNull(userController.addUser(user2).getBody()).getId();

        //when
        filmController.addLikeByUser(filmId, userId);
        filmController.addLikeByUser(filmId2, userId2);
        filmController.addLikeByUser(filmId2, userId2);
        List<FilmDto> popularFilms = filmController.getMostPopularFilms(FILM_COUNT_1).getBody();

        //then
        assertNotNull(popularFilms, "В контроллере нет популярных фильмов");
        assertEquals(1, popularFilms.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME_2, popularFilms.get(0).getName(), "В контроллере не корректный фильм");
    }

    @Test
    @DisplayName("Если передано дефолтное количество фильмов, то должен вернуть все (до дефолтного включительно)")
    void test_getMostPopularFilms_WhenRequestNothing_ShouldReturnAll() {
        //given
        NewFilmRequest film = NewFilmRequest.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        NewFilmRequest film2 = NewFilmRequest.builder().name(FILM_NAME_2).description(FILM_DESCRIPTION_2)
                .releaseDate(FILM_RELEASE_DATE_2)
                .duration(FILM_DURATION_2).build();

        int filmId = Objects.requireNonNull(filmController.addFilm(film).getBody()).getId();
        int filmId2 = Objects.requireNonNull(filmController.addFilm(film2).getBody()).getId();

        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY).build();
        int userId2 = Objects.requireNonNull(userController.addUser(user2).getBody()).getId();

        //when
        filmController.addLikeByUser(filmId, userId);
        filmController.addLikeByUser(filmId2, userId2);
        filmController.addLikeByUser(filmId2, userId2);
        List<FilmDto> popularFilms = filmController.getMostPopularFilms(FILM_DEFAULT_COUNT).getBody();

        //then
        assertNotNull(popularFilms, "В контроллере нет популярных фильмов");
        assertEquals(2, popularFilms.size(), "В контроллере не верное количество фильмов");
        assertEquals(FILM_NAME_2, popularFilms.get(0).getName(), "В контроллере не корректный 1-ый фильм");
        assertEquals(FILM_NAME, popularFilms.get(1).getName(), "В контроллере не корректный 2-ой фильм");
    }
}