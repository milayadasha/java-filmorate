package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final int FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Integer, Film> films = new HashMap<>();

    /**
     * Возвращает все фильмы в виде списка
     */
    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    /**
     * Добавляет новый фильм.
     * Проверяет поля фильма на соответствие.
     * Если всё хорошо, то создаёт копию переданного фильма, присваивает уникальный ID и сохраняет в контроллер.
     *
     * @param newFilm объект фильма, который нужно добавить
     * @return копия созданного фильма с присвоенным ID
     */
    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film newFilm) {
        try {
            checkIsValidFilm(newFilm);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных фильма {} при добавлении: {}", newFilm.getId(), exception.getMessage());
            throw exception;
        }
        log.trace("Данные фильма {} прошли валидацию при добавлении", newFilm.getId());

        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм {} добавлен", newFilm.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(newFilm);
    }

    /**
     * Обновляет фильм.
     * Проверяет поля переданного фильма на соответствие.
     * Если всё хорошо и переданный фильм существует в контроллере, то создаёт копию и сохраняет в контроллер.
     *
     * @param updatedFilm объект фильма, который нужно обновить
     * @return копия обновлённого фильма
     */
    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updatedFilm) {
        try {
            checkIsValidFilm(updatedFilm);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных фильма {} при обновлении: {}", updatedFilm.getId(),
                    exception.getMessage());
            throw exception;
        }

        log.trace("Фильм {} прошёл валидацию для обновления", updatedFilm.getId());
        if (!films.containsKey(updatedFilm.getId())) {
            String filmNotFound = "Фильм " + updatedFilm.getId() + " для обновления не найден";
            log.error(filmNotFound);
            throw new ValidationException(filmNotFound);
        }

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Фильм {} обновлён", updatedFilm.getId());

        return ResponseEntity.ok(updatedFilm);
    }

    /**
     * Генерирует следующий Id.
     * Находит максимальный текущй Id и увеличивает его.
     */
    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    /**
     * Проверяет переданный фильм на соответствие условиям.
     * Если не удовлетворяет какой-то проверке, то выбрасывается ошибка
     */
    private void checkIsValidFilm(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("Фильм не найден");
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