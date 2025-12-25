package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
public class User {
    private int id;
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String login;

    private LocalDate birthday;

    Set<Integer> friends;
}