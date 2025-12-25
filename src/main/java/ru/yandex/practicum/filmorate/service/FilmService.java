package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private static final int FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Возвращает фильм по id.
     * Вызывает метод хранилища по получению фильма по id.
     */
    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    /**
     * Возвращает все фильмы в виде списка.
     * Вызывает метод хранилища по получению всех фильмов
     */
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    /**
     * Добавляет новый фильм.
     * Проверяет поля переданного фильма на соответствие.
     * Если всё хорошо, то вызывает соответствующий метод хранилища.
     */
    public Film addFilm(Film newFilm) {
        try {
            checkIsValidFilm(newFilm);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных фильма {} при добавлении: {}", newFilm.getId(), exception.getMessage());
            throw exception;
        }
        log.trace("Данные фильма {} прошли валидацию при добавлении", newFilm.getId());
        return filmStorage.addFilm(newFilm);
    }

    /**
     * Обновляет фильм.
     * Проверяет поля переданного фильма на соответствие.
     * Если всё хорошо, то вызывает соответствующий метод хранилища.
     */
    public Film updateFilm(Film updatedFilm) {
        try {
            checkIsValidFilm(updatedFilm);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных фильма {} при обновлении: {}", updatedFilm.getId(),
                    exception.getMessage());
            throw exception;
        }
        log.trace("Фильм {} прошёл валидацию для обновления", updatedFilm.getId());
        return filmStorage.updateFilm(updatedFilm);
    }

    /**
     * Добавляет лайк к фильму.
     * Если пользователь и фильм существуют, то добавляет id пользователя в список тех, кто его лайкнул.
     * Вызывает метод хранилища по обновлению фильма.
     */
    public void addLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        log.trace("Пользователь {} найден для добавления лайка", userId);
        Film film = filmStorage.getFilmById(filmId);
        log.trace("Фильм {} найден для добавления лайка", filmId);

        film.getLikes().add(userId);
        log.info("Добавление лайка от пользователя {} для фильма {} выполнено", userId, filmId);

        filmStorage.updateFilm(film);
    }

    /**
     * Убирает лайк у фильма.
     * Если фильм существу, то убирает id пользователя из списка тех, кто его лайкнул.
     * Вызывает метод хранилища по обновлению фильма.
     */
    public void removeLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        log.trace("Пользователь {} найден для удаления лайка", userId);
        Film film = filmStorage.getFilmById(filmId);
        log.trace("Фильм {} найден для удаления лайка", filmId);

        film.getLikes().remove(userId);
        log.info("Удаление лайка от пользователя {} для фильма {} выполнено", userId, filmId);

        filmStorage.updateFilm(film);
    }

    /**
     * Возвращает список самых популярных фильмов в виде списка.
     * Вызывает метод хранилища по получению всех фильмов, сортирует их и фильтрует по количеству.
     */
    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(
                        (Film film) -> film.getLikes().size()).reversed().thenComparingInt(Film::getId))
                .limit(count)
                .toList();
    }

    /**
     * Проверяет переданный фильм на соответствие условиям.
     * Если не удовлетворяет какой-то проверке, то выбрасывается ошибка
     */
    private void checkIsValidFilm(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("Фильм для валидации входных параметров не найден");
        }

        if (film.getDescription().length() > FILM_DESCRIPTION_LENGTH) {
            throw new ValidationException("Описание фильма не может быть больше 200 символов");
        }

        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
