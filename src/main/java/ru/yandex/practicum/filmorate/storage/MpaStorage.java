package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    /**
     * Возвращает MPA рейтинг по id
     */
    Mpa getMpaById(int id);

    /**
     * Возвращает все MPA рейтинги в виде списка
     */
    List<Mpa> getMpaList();
}
