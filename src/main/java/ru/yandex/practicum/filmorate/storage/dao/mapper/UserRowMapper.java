package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        Set<Integer> friends = new HashSet<>();

        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .login(login)
                .birthday(birthday)
                .friends(friends)
                .build();
    }
}