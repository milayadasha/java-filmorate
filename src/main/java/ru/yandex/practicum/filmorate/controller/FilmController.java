package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
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
    public ResponseEntity<FilmDto> getFilmById(@PathVariable int id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    /**
     * Возвращает все фильмы в виде списка
     */
    @GetMapping
    public ResponseEntity<List<FilmDto>> getFilms() {
        return ResponseEntity.ok(filmService.getFilms());
    }

    /**
     * Добавляет новый фильм.
     *
     * @param newFilmDto объект фильма, который нужно добавить
     * @return ответ, содержащий созданный фильм с присвоенным ID
     */
    @PostMapping
    public ResponseEntity<FilmDto> addFilm(@Valid @RequestBody NewFilmRequest newFilmDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(newFilmDto));
    }

    /**
     * Обновляет фильм.
     *
     * @param updatedFilmDto объект фильма, который нужно обновить
     * @return ответ, содержащий обновлённый фильм
     */
    @PutMapping
    public ResponseEntity<FilmDto> updateFilm(@Valid @RequestBody UpdateFilmRequest updatedFilmDto) {
        return ResponseEntity.ok(filmService.updateFilm(updatedFilmDto));
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
    public ResponseEntity<List<FilmDto>> getMostPopularFilms(
            @RequestParam(required = false, defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.getMostPopularFilms(count));
    }
}