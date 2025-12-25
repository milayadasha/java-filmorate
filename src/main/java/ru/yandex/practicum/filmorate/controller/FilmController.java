package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Возвращает фильм по id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    /**
     * Возвращает все фильмы в виде списка
     */
    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(filmService.getFilms());
    }

    /**
     * Добавляет новый фильм.
     *
     * @param newFilm объект фильма, который нужно добавить
     * @return ответ, содержащий созданный фильм с присвоенным ID
     */
    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film newFilm) {
        Film createdFilm = filmService.addFilm(newFilm);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    /**
     * Обновляет фильм.
     *
     * @param updatedFilm объект фильма, который нужно обновить
     * @return ответ, содержащий обновлённый фильм
     */
    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updatedFilm) {
        Film savedFilm = filmService.updateFilm(updatedFilm);
        return ResponseEntity.ok(savedFilm);
    }

    /**
     * Добавляет лайк к фильму
     *
     * @param id     идентификатор фильма, которому нужно добавить лайк.
     * @param userId идентификатор пользователя, кто поставил лайк.
     * @return пустой ответ со статусом 200
     */
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLikeByUser(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Убирает лайк с фильма
     *
     * @param id     идентификатор фильма, которому нужно убрать лайк.
     * @param userId идентификатор пользователя, чей лайк надо убрать.
     * @return пустой ответ со статусом 200
     */
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLikeByUser(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает самые популярные фильмы в виде списка.
     *
     * @param count количество фильмов, которое максимально надо вернуть
     * @return список самых популярных фильмов
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getMostPopularFilms(
            @RequestParam(required = false, defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.getMostPopularFilms(count));
    }
}