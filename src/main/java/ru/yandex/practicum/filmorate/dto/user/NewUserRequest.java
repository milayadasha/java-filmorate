package ru.yandex.practicum.filmorate.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;
}