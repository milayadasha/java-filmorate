package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.MpaRowMapper;

import java.util.List;

@Repository
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {
    private static final Logger log = LoggerFactory.getLogger(MpaDbStorage.class);
    private static final String GET_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?;";
    private static final String GET_ALL_MPA = "SELECT * FROM mpa_ratings ORDER BY id;";

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    /**
     * Возвращает MPA рейтинг по id
     */
    public Mpa getMpaById(int id) {
        return findOne(GET_MPA_BY_ID, id).orElseThrow(() -> {
            String mpaNotFound = "Фильм с id = " + id + " не найден";
            log.error(mpaNotFound);
            return new NotFoundException(mpaNotFound);
        });
    }

    /**
     * Возвращает все MPA рейтинги в виде списка
     */
    public List<Mpa> getMpaList() {
        return findMany(GET_ALL_MPA);
    }
}
