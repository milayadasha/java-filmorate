package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

public interface GenreStorage {
    /**
     * Возвращает жанр по id
     */
    Genre getGenreById(int id);

    /**
     * Возвращает все жанры в виде списка
     */
    List<Genre> getGenres();
}
