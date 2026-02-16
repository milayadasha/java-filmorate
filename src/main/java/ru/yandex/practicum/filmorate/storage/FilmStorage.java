package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    /**
     * Возвращает фильм по id
     */
    Film getFilmById(int id);

    /**
     * Возвращает все фильмы в виде списка
     */
    List<Film> getFilms();

    /**
     * Добавляет новый фильм.
     */
    Film addFilm(Film newFilm);

    /**
     * Обновляет фильм.
     */
    Film updateFilm(Film updatedFilm);

    /**
     * Добавляет лайк.
     */
    void addLike(int filmId, int userId);

    /**
     * Удаляет лайк.
     */
    void removeLike(int filmId, int userId);

    /**
     * Возвращает популярные фильмы
     */
    List<Film> getMostPopularFilms(int count);
}
