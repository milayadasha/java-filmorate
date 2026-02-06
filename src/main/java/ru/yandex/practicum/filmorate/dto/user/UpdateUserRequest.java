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
public class UpdateUserRequest {
    int id;
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;

    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasBirthday() {
        return ! (birthday == null);
    }
}
