package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private static final String USER_NAME = "Вася";
    private static final String USER_NAME_2 = "Петя";

    private static final String USER_EMAIL = "mail@mail.ru";
    private static final String USER_EMAIL_2 = "newMail@mail.ru";

    private static final String USER_LOGIN = "user";
    private static final String USER_LOGIN_2 = "newUser";
    private static final String USER_LOGIN_INCORRECT = "new User";

    private static final LocalDate USER_BIRTHDAY = LocalDate.of(1992, Month.DECEMBER, 12);
    private static final LocalDate USER_BIRTHDAY_2 = LocalDate.of(1982, Month.OCTOBER, 22);
    private static final LocalDate USER_BIRTHDAY_INCORRECT = LocalDate.of(2025, Month.DECEMBER, 12);

    UserController userController;

    @BeforeEach
    @DisplayName("Инициализирует контроллер")
    void setUp() {
        userController = new UserController();
    }

    @Test
    @DisplayName("При добавлении двух пользователей контроллер должен возвращать их")
    void test_getUsers_WhenAddedUsers_ShouldReturnAll() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User user2 = User.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2).birthday(USER_BIRTHDAY_2)
                .build();

        //when
        userController.addUser(user);
        userController.addUser(user2);
        List<User> usersByController = userController.getUsers().stream().toList();

        //then
        assertEquals(2, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_NAME, usersByController.get(0).getName(),
                "В контроллере не корректный 1-ый пользователь");
        assertEquals(USER_NAME_2, usersByController.get(1).getName(),
                "В контроллере не корректный 2-ой пользователь");
    }

    @Test
    @DisplayName("При добавлении пользователя с корректными полями контроллер должен добавить его")
    void test_addUser_WhenCorrectFields_ShouldAddToController() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();

        //when
        userController.addUser(user);
        List<User> usersByController = userController.getUsers().stream().toList();

        //then
        assertEquals(1, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_NAME, usersByController.get(0).getName(), "В контроллере некорректный пользователь");
    }

    @Test
    @DisplayName("При добавлении пользователя с некорректным логином контроллер должен выбросить ошибку")
    void test_addUser_WhenIncorrectLogin_ShouldThrowsError() {
        //given && when
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN_INCORRECT)
                .birthday(USER_BIRTHDAY).build();

        //then
        assertThrows(ValidationException.class,
                () -> userController.addUser(user),
                "Пользователь добавлен с некорректным логином");
    }

    @Test
    @DisplayName("При добавлении пользователя без имени в контроллере имя будет равно логину")
    void test_addUser_WhenNameIsNull_ShouldReturnNameWithLoginValue() {
        //given && when
        User user = User.builder().email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();

        //when
        userController.addUser(user);
        List<User> usersByController = userController.getUsers().stream().toList();

        //then
        assertEquals(1, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_LOGIN, usersByController.get(0).getName(), "Имя пользователя не равно логину");
    }

    @Test
    @DisplayName("При добавлении пользователя с датой рождения в будущем контроллер должен выбросить ошибку")
    void test_addUser_WhenBirthdayInFuture_ShouldThrowsError() {
        //given && when
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY_INCORRECT).build();

        //then
        assertThrows(ValidationException.class,
                () -> userController.addUser(user),
                "Пользователь добавлен с датой рождения в будущем");
    }

    @Test
    @DisplayName("При обновлении пользователя с корректными полями контроллер должен обновить его")
    void test_updateUser_WhenCorrectFields_ShouldUpdateInController() {
        //given
        User user = userController.addUser(User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build());

        //when
        User updateUser = user.toBuilder().name(USER_NAME_2).build();
        userController.updateUser(updateUser);
        List<User> usersByController = userController.getUsers().stream().toList();

        //then
        assertEquals(1, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_NAME_2, usersByController.get(0).getName(), "В контроллере некорректный пользователь");
    }

    @Test
    @DisplayName("При обновлении пользователя с некорректным логином контроллер должен выбросить ошибку")
    void test_updateUser_WhenIncorrectLogin_ShouldThrowsError() {
        //given && when
        User user = userController.addUser(User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build());

        //when
        User updateUser = user.toBuilder().login(USER_LOGIN_INCORRECT).build();

        //then
        assertThrows(ValidationException.class,
                () -> userController.updateUser(updateUser),
                "Пользователь обновлён с некорректным логином");
    }

    @Test
    @DisplayName("При обновлении пользователя без имени в контроллере имя будет равно логину")
    void test_updateUser_WhenNameIsNull_ShouldReturnNameWithLoginValue() {
        //given && when
        User user = userController.addUser(User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build());

        //when
        User updateUser = user.toBuilder().name("").build();
        userController.updateUser(updateUser);
        List<User> usersByController = userController.getUsers().stream().toList();

        //then
        assertEquals(1, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_LOGIN, usersByController.get(0).getName(), "Имя пользователя не равно логину");
    }

    @Test
    @DisplayName("При обновлении пользователя с датой рождения в будущем контроллер должен выбросить ошибку")
    void test_updateUser_WhenBirthdayInFuture_ShouldThrowsError() {
        //given && when
        User user = userController.addUser(User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY).build());

        //when
        User updateUser = user.toBuilder().birthday(USER_BIRTHDAY_INCORRECT).build();

        //then
        assertThrows(ValidationException.class,
                () -> userController.updateUser(updateUser),
                "Пользователь обновлён с датой рождения в будущем");
    }

}