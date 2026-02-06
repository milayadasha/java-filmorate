package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.GenreRowMapper;

import java.util.List;

@Repository
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final Logger log = LoggerFactory.getLogger(GenreDbStorage.class);
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?;";
    private static final String GET_ALL_GENRES = "SELECT * FROM genres ORDER BY id;";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    /**
     * Возвращает жанр по id
     */
    public Genre getGenreById(int id) {
        return findOne(GET_GENRE_BY_ID, id).orElseThrow(() -> {
            String genreNotFound = "Жанр с id = " + id + " не найден";
            log.error(genreNotFound);
            return new NotFoundException(genreNotFound);
        });
    }

    /**
     * Возвращает все жанры в виде списка
     */
    public List<Genre> getGenres() {
        return findMany(GET_ALL_GENRES);
    }
}
