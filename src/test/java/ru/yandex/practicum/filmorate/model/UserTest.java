package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    private static final int USER_ID = 1;
    private static final String USER_NAME = "Имя";
    private static final String USER_EMAIL = "test@mail.ru";
    private static final String USER_LOGIN = "user";
    private static final LocalDate USER_BIRTHDAY = LocalDate.of(1992, Month.DECEMBER, 12);

    @Test
    @DisplayName("Проверяет, что пользователь создаётся с переданными параметрами")
    public void test_createUser_WhenCreateShouldNotEmptyValues() {
        //given && when
        User user = User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build();

        //then
        assertEquals(USER_ID, user.getId(), "Пользователь создался с неверным id");
        assertEquals(USER_NAME, user.getName(), "Пользователь создался с неверным именем");
        assertEquals(USER_EMAIL, user.getEmail(), "Пользователь создался с неверной почтой");
        assertEquals(USER_LOGIN, user.getLogin(), "Пользователь создался с неверным логином");
        assertEquals(USER_BIRTHDAY, user.getBirthday(), "Пользователь создался с неверной датой рождения");
    }

}