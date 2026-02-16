package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class FilmDbStorageTest {
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

    private static final String USER_NAME = "Вася";
    private static final String USER_EMAIL = "mail@mail.ru";
    private static final String USER_EMAIL_2 = "newMail@mail.ru";
    private static final String USER_LOGIN = "user";
    private static final String USER_LOGIN_2 = "newUser";
    private static final LocalDate USER_BIRTHDAY = LocalDate.of(1992, Month.DECEMBER, 12);
    private static final Integer USER_ID = 567;

    private static final String DELETE_FILMS = "DELETE FROM films;";
    private static final String DELETE_USERS = "DELETE FROM users;";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    @DisplayName("Чистим БД")
    void cleanDatabase() {
        jdbcTemplate.update(DELETE_FILMS);
        jdbcTemplate.update(DELETE_USERS);
    }

    @Test
    @DisplayName("При получении существующего фильма по id хранилище должно вернуть его")
    void test_getFilmById_WhenFilmExists_ShouldReturnOne() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        Film createdFilm = filmStorage.addFilm(film);

        //when
        Film findFilm = filmStorage.getFilmById(createdFilm.getId());

        //then
        assertNotNull(findFilm, "В БД нет фильмов");
        assertEquals(FILM_NAME, findFilm.getName(), "В БД некорректный фильм");
    }

    @Test
    @DisplayName("При получении несуществующего фильма по id хранилище должно вернуть ошибку")
    void test_getFilmById_WhenFilmNotExists_ShouldThrowsError() {
        //given && when && then
        assertThrows(NotFoundException.class,
                () -> filmStorage.getFilmById(FILM_ID),
                "Из БД получен несуществующий фильм");
    }

    @Test
    @DisplayName("При добавлении двух фильмов хранилище должно возвращать их")
    void test_getFilms_WhenAddedFilms_ShouldReturnAll() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        Film film2 = Film.builder().name(FILM_NAME_2).description(FILM_DESCRIPTION_2).releaseDate(FILM_RELEASE_DATE_2)
                .duration(FILM_DURATION_2).build();

        //when
        filmStorage.addFilm(film);
        filmStorage.addFilm(film2);
        List<Film> filmsByController = filmStorage.getFilms();

        //then
        assertNotNull(filmsByController, "В БД нет фильмов");
        assertEquals(2, filmsByController.size(), "В БД не верное количество фильмов");
        assertEquals(FILM_NAME, filmsByController.get(0).getName(), "В БД не корректный 1-ый фильм");
        assertEquals(FILM_NAME_2, filmsByController.get(1).getName(), "В БД не корректный 2-ой фильм");
    }

    @Test
    @DisplayName("При добавлении фильма с корректными полями хранилище должно добавить его")
    void test_addFilm_WhenCorrectFields_ShouldAddToController() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();

        //when
        filmStorage.addFilm(film);
        List<Film> filmsByStorage = filmStorage.getFilms();

        //then
        assertNotNull(filmsByStorage, "В БД нет фильмов");
        assertEquals(1, filmsByStorage.size(), "В БД не верное количество фильмов");
        assertEquals(FILM_NAME, filmsByStorage.get(0).getName(), "В БД некорректный фильм");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректным описанием хранилище должно выбросить ошибку")
    void test_addFilm_WhenIncorrectDescription_ShouldThrowsError() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION_INCORRECT)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();

        //then
        assertThrows(DataIntegrityViolationException.class,
                () -> filmStorage.addFilm(film),
                "Фильм может быть добавлен с некорректным описанием");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректной длительностью хранилище должно выбросить ошибку")
    void test_addFilm_WhenIncorrectDuration_ShouldThrowsError() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION_INCORRECT).build();

        //then
        assertThrows(DataIntegrityViolationException.class,
                () -> filmStorage.addFilm(film),
                "Фильм может быть добавлен с некорректной длительностью");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректной датой релиза хранилище должно выбросить ошибку")
    void test_addFilm_WhenIncorrectReleaseDate_ShouldThrowsError() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION)
                .releaseDate(FILM_RELEASE_DATE_INCORRECT)
                .duration(FILM_DURATION).build();

        //then
        assertThrows(DataIntegrityViolationException.class,
                () -> filmStorage.addFilm(film),
                "Фильм может быть добавлен с некорректной датой релиза");
    }

    @Test
    @DisplayName("При обновлении фильма с корректными данными хранилище должно обновить его")
    void test_updateFilm_WhenCorrectFields_ShouldUpdateInController() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        filmStorage.addFilm(film);

        //when
        Film updatedFilm = film.toBuilder().name(FILM_NAME_2).build();
        filmStorage.updateFilm(updatedFilm);
        List<Film> filmsByController = filmStorage.getFilms();

        //then
        assertNotNull(filmsByController, "В БД нет фильмов");
        assertEquals(1, filmsByController.size(), "В БД не верное количество фильмов");
        assertEquals(FILM_NAME_2, filmsByController.get(0).getName(), "В БД некорректный фильм");
    }

    @Test
    @DisplayName("При обновлении фильма с некорректным описанием хранилище должно выбросить ошибку")
    void test_updateFilm_WhenIncorrectDescription_ShouldThrowsError() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        filmStorage.addFilm(film);

        //when
        Film updatedFilm = film.toBuilder().description(FILM_DESCRIPTION_INCORRECT).build();

        //then
        assertThrows(DataIntegrityViolationException.class,
                () -> filmStorage.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректным описанием");
    }

    @Test
    @DisplayName("При обновлении фильма с некорректной длительностью хранилище должно выбросить ошибку")
    void test_updateFilm_WhenIncorrectDuration_ShouldThrowsError() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        filmStorage.addFilm(film);

        //when
        Film updatedFilm = film.toBuilder().duration(FILM_DURATION_INCORRECT).build();

        //then
        assertThrows(DataIntegrityViolationException.class,
                () -> filmStorage.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректной длительностью");
    }

    @Test
    @DisplayName("При добавлении фильма с некорректной датой релиза хранилище должно выбросить ошибку")
    void test_updateFilm_WhenIncorrectReleaseDate_ShouldThrowsError() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        filmStorage.addFilm(film);

        //when
        Film updatedFilm = film.toBuilder().releaseDate(FILM_RELEASE_DATE_INCORRECT).build();

        //then
        assertThrows(DataIntegrityViolationException.class,
                () -> filmStorage.updateFilm(updatedFilm),
                "Фильм может быть обновлён с некорректной датой релиза");
    }

    @Test
    @DisplayName("При добавлении лайка существующему фильму от существующего пользователя должен вернуться OK")
    void test_addLikeByUser_WhenFilmAndUserExist_ShouldReturn200() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        Film createdFilm = filmStorage.addFilm(film);

        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User createdUser = userStorage.addUser(user);

        //then
        assertDoesNotThrow(() -> filmStorage.addLike(createdFilm.getId(), createdUser.getId()),
                "Не удалось поставить лайк существующему фильму");
    }

    @Test
    @DisplayName("При добавлении лайка несуществующему фильму от существующего пользователя должен вернуть ошибку")
    void test_addLikeByUser_WhenFilmNotExists_ShouldThrowsError() {
        //given && when
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User createdUser = userStorage.addUser(user);

        //then
        assertThrows(DataIntegrityViolationException.class, () -> filmStorage.addLike(FILM_ID, createdUser.getId()),
                "Удалось поставить лайк несуществующему фильму");
    }

    @Test
    @DisplayName("При удалении лайка с существующего фильма от существующего пользователя должен вернуть OK")
    void test_removeLikeByUser_WhenFilmAndUserExist_ShouldReturnOk() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        Film createdFilm = filmStorage.addFilm(film);

        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User createdUser = userStorage.addUser(user);

        //when
        filmStorage.addLike(createdFilm.getId(), createdUser.getId());

        //then
        assertDoesNotThrow(() -> filmStorage.removeLike(createdFilm.getId(), createdUser.getId()),
                "Не удалось поставить лайк существующему фильму");
    }

    @Test
    @DisplayName("При удалении лайка с существующего фильма от несуществующего пользователя должен вернуть ошибку")
    void test_removeLikeByUser_WhenUserNotExists_ShouldThrowsError() {
        //given && when
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        Film createdFilm = filmStorage.addFilm(film);

        //then
        assertThrows(InternalServerException.class, () -> filmStorage.removeLike(createdFilm.getId(), USER_ID),
                "Удалось убрать лайк от несуществующего пользователя");
    }

    @Test
    @DisplayName("При удалении лайка с несуществующего фильма от существующего пользователя должен вернуть ошибку")
    void test_removeLikeByUser_WhenFilmNotExists_ShouldThrowsError() {
        //given && when
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User createdUser = userStorage.addUser(user);

        //then
        assertThrows(InternalServerException.class, () -> filmStorage.removeLike(FILM_ID, createdUser.getId()),
                "Удалось убрать лайк с несуществующего фильма");
    }

    @Test
    @DisplayName("При запросе одного фильма должен вернуть самый популярный")
    void test_getMostPopularFilms_WhenRequestOne_ShouldReturnOne() {
        //given
        Film film = Film.builder().name(FILM_NAME).description(FILM_DESCRIPTION).releaseDate(FILM_RELEASE_DATE)
                .duration(FILM_DURATION).build();
        Film film2 = Film.builder().name(FILM_NAME_2).description(FILM_DESCRIPTION_2).releaseDate(FILM_RELEASE_DATE_2)
                .duration(FILM_DURATION_2).build();
        Film createdFilm = filmStorage.addFilm(film);
        Film createdFilm2 = filmStorage.addFilm(film2);

        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User createdUser = userStorage.addUser(user);
        User user2 = User.builder().name(USER_NAME).email(USER_EMAIL_2).login(USER_LOGIN_2).birthday(USER_BIRTHDAY).build();
        User createdUser2 = userStorage.addUser(user2);

        //when
        filmStorage.addLike(createdFilm.getId(), createdUser.getId());
        filmStorage.addLike(createdFilm2.getId(), createdUser.getId());
        filmStorage.addLike(createdFilm2.getId(), createdUser2.getId());
        List<Film> popularFilms = filmStorage.getMostPopularFilms(FILM_COUNT_1);

        //then
        assertNotNull(popularFilms, "В БД нет популярных фильмов");
        assertEquals(1, popularFilms.size(), "В БД не верное количество фильмов");
        assertEquals(FILM_NAME_2, popularFilms.get(0).getName(), "В БД не корректный фильм");
    }
}