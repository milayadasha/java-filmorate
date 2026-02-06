package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        int duration = resultSet.getInt("duration");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .releaseDate(releaseDate)
                .build();
    }
}