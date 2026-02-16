package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserDto {
    private int id;
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;
}
