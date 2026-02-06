package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class UserControllerTest {
    private static final String USER_NAME = "Вася";
    private static final String USER_NAME_2 = "Петя";
    private static final String USER_NAME_3 = "Коля";

    private static final String USER_EMAIL = "mail@mail.ru";
    private static final String USER_EMAIL_2 = "newMail@mail.ru";
    private static final String USER_EMAIL_3 = "newNewMail@mail.ru";

    private static final String USER_LOGIN = "user";
    private static final String USER_LOGIN_2 = "newUser";
    private static final String USER_LOGIN_3 = "friendUser";
    private static final String USER_LOGIN_INCORRECT = "new User";

    private static final LocalDate USER_BIRTHDAY = LocalDate.of(1992, Month.DECEMBER, 12);
    private static final LocalDate USER_BIRTHDAY_2 = LocalDate.of(1982, Month.OCTOBER, 22);
    private static final LocalDate USER_BIRTHDAY_3 = LocalDate.of(1968, Month.NOVEMBER, 2);
    private static final LocalDate USER_BIRTHDAY_INCORRECT = LocalDate.of(3035, Month.DECEMBER, 12);

    private static final Integer USER_ID = 567;

    private static final String DELETE_USERS = "DELETE FROM users;";

    private final UserController userController;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    @DisplayName("Чистим БД")
    void cleanDatabase() {
        jdbcTemplate.update(DELETE_USERS);
    }

    @Test
    @DisplayName("При добавлении двух пользователей контроллер должен возвращать их")
    void test_getUsers_WhenAddedUsers_ShouldReturnAll() {
        //given
        //when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        userController.addUser(user);

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY_2)
                .build();
        userController.addUser(user2);
        List<UserDto> usersByController = userController.getUsers().getBody();

        //then
        assertNotNull(usersByController, "В контроллере нет пользователей");
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
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();

        //when
        userController.addUser(user);
        List<UserDto> usersByController = userController.getUsers().getBody();

        //then
        assertNotNull(usersByController, "В контроллере нет пользователей");
        assertEquals(1, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_NAME, usersByController.get(0).getName(), "В контроллере некорректный пользователь");
    }

    @Test
    @DisplayName("При добавлении пользователя с некорректным логином контроллер должен выбросить ошибку")
    void test_addUser_WhenIncorrectLogin_ShouldThrowsError() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN_INCORRECT)
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
        NewUserRequest user = NewUserRequest.builder().email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();

        //when
        userController.addUser(user);
        List<UserDto> usersByController = userController.getUsers().getBody();

        //then
        assertNotNull(usersByController, "В контроллере нет пользователей");
        assertEquals(1, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_LOGIN, usersByController.get(0).getName(), "Имя пользователя не равно логину");
    }

    @Test
    @DisplayName("При добавлении пользователя с датой рождения в будущем контроллер должен выбросить ошибку")
    void test_addUser_WhenBirthdayInFuture_ShouldThrowsError() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
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
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //when
        UpdateUserRequest updateUser = UpdateUserRequest.builder().id(userId).name(USER_NAME_2).email(USER_EMAIL)
                .login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        userController.updateUser(updateUser);
        List<UserDto> usersByController = userController.getUsers().getBody();

        //then
        assertNotNull(usersByController, "В контроллере нет пользователей");
        assertEquals(1, usersByController.size(), "В контроллере не верное количество пользователей");
        assertEquals(USER_NAME_2, usersByController.get(0).getName(), "В контроллере некорректный пользователь");
    }

    @Test
    @DisplayName("При обновлении пользователя с некорректным логином контроллер должен выбросить ошибку")
    void test_updateUser_WhenIncorrectLogin_ShouldThrowsError() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //when
        UpdateUserRequest updateUser = UpdateUserRequest.builder().id(userId).name(USER_NAME).email(USER_EMAIL)
                .login(USER_LOGIN_INCORRECT)
                .birthday(USER_BIRTHDAY)
                .build();

        //then
        assertThrows(ValidationException.class,
                () -> userController.updateUser(updateUser),
                "Пользователь обновлён с некорректным логином");
    }


    @Test
    @DisplayName("При обновлении пользователя с датой рождения в будущем контроллер должен выбросить ошибку")
    void test_updateUser_WhenBirthdayInFuture_ShouldThrowsError() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //when
        UpdateUserRequest updateUser = UpdateUserRequest.builder().id(userId).name(USER_NAME).email(USER_EMAIL)
                .login(USER_LOGIN)
                .birthday(USER_BIRTHDAY_INCORRECT)
                .build();

        //then
        assertThrows(ValidationException.class,
                () -> userController.updateUser(updateUser),
                "Пользователь обновлён с датой рождения в будущем");
    }

    @Test
    @DisplayName("При получении существующего пользователя по id контроллер должен вернуть его")
    void test_getUserById_WhenUserExists_ShouldReturnOne() {
        //given
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();


        //when
        UserDto findUser = userController.getUserById(userId).getBody();

        //then
        assertNotNull(findUser, "В контроллере нет пользователей");
        assertEquals(USER_NAME, findUser.getName(), "В контроллере некорректный пользователь");
    }

    @Test
    @DisplayName("При получении несуществующего пользователя по id контроллер должен выбросить ошибку")
    void test_getUserById_WhenUserNotExists_ShouldThrowsError() {
        //given && when && then
        assertThrows(NotFoundException.class,
                () -> userController.getUserById(USER_ID),
                "Из контроллера получен несуществующий пользователь");
    }

    @Test
    @DisplayName("При добавлении в друзья существующих пользователей ответ должен быть OK 200")
    void test_addFriend_WhenUsersExist_ShouldReturn200() {
        //given
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY_2)
                .build();
        int userId2 = Objects.requireNonNull(userController.addUser(user2).getBody()).getId();

        //when
        ResponseEntity<Void> response = userController.addFriend(userId, userId2);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "При добавлении в друзья вернулся не успешный ответ");
    }

    @Test
    @DisplayName("При добавлении в друзья несуществующего пользователя контроллер должен выбросить ошибку")
    void test_addFriend_WhenOneUserNotExists_ShouldThrowsError() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //then
        assertThrows(NotFoundException.class,
                () -> userController.addFriend(userId, USER_ID),
                "Удалось добавить в друзья не существующего пользователя");
    }

    @Test
    @DisplayName("При удалении из друзей существующих пользователей ответ должен быть OK 200")
    void test_removeFriend_WhenFriendsExist_ShouldReturn200() {
        //given
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY_2)
                .build();
        int userId2 = Objects.requireNonNull(userController.addUser(user2).getBody()).getId();
        userController.addFriend(userId, userId2);

        //when
        ResponseEntity<Void> response = userController.removeFriend(userId, userId2);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "При удалении из друзей вернулся не успешный ответ");
    }

    @Test
    @DisplayName("При удалении из друзей несуществующего пользователя контроллер должен выбросить ошибку")
    void test_removeFriend_WhenOneFriendNotExist_ShouldReturn200() {
        //given && when
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //then
        assertThrows(NotFoundException.class,
                () -> userController.removeFriend(userId, USER_ID),
                "Удалось удалить из друзей несуществующего пользователя");
    }

    @Test
    @DisplayName("При получении всех друзей пользователя должен возвращаться корректный список")
    void test_getAllFriends_WhenAddFriends_ShouldReturnOne() {
        //given
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY_2)
                .build();
        int userId2 = Objects.requireNonNull(userController.addUser(user2).getBody()).getId();

        //when
        userController.addFriend(userId, userId2);
        List<UserDto> userFriends = userController.getAllFriends(userId).getBody();

        //then
        assertNotNull(userFriends, "У пользователя нет друзей");
        assertEquals(USER_NAME_2, userFriends.get(0).getName(), "В друзьях некорректный пользователь");
    }

    @Test
    @DisplayName("При отсутствии друзей у пользователя должен возвращаться пустой список")
    void test_getAllFriends_WhenNoFriends_ShouldReturnEmpty() {
        //given
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        //when
        List<UserDto> userFriends = userController.getAllFriends(userId).getBody();

        //then
        assertNotNull(userFriends, "Список друзей не проинициализировался");
        assertEquals(0, userFriends.size(), "Список друзей не пустой");
    }

    @Test
    @DisplayName("При получении общих друзей пользователей должен возвращаться корректный список")
    void test_getCommonFriends_WhenAddSameFriend_ShouldReturnOne() {
        //given
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY_2)
                .build();
        int userId2 = Objects.requireNonNull(userController.addUser(user2).getBody()).getId();

        NewUserRequest user3 = NewUserRequest.builder().name(USER_NAME_3).email(USER_EMAIL_3).login(USER_LOGIN_3)
                .birthday(USER_BIRTHDAY_3)
                .build();
        int userId3 = Objects.requireNonNull(userController.addUser(user3).getBody()).getId();

        //when
        userController.addFriend(userId, userId3);
        userController.addFriend(userId2, userId3);
        List<UserDto> commonFriends = userController.getCommonFriends(userId, userId2).getBody();

        //then
        assertNotNull(commonFriends, "У пользователя нет общих друзей");
        assertEquals(USER_NAME_3, commonFriends.get(0).getName(), "В общих друзьях некорректный пользователь");
    }

    @Test
    @DisplayName("При отсутствии общих друзей у пользователей должен возвращаться пустой список")
    void test_getCommonFriends_WhenAddNotSameFriend_ShouldReturnEmpty() {
        //given
        NewUserRequest user = NewUserRequest.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN)
                .birthday(USER_BIRTHDAY)
                .build();
        int userId = Objects.requireNonNull(userController.addUser(user).getBody()).getId();

        NewUserRequest user2 = NewUserRequest.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2)
                .birthday(USER_BIRTHDAY_2)
                .build();
        int userId2 = Objects.requireNonNull(userController.addUser(user2).getBody()).getId();

        NewUserRequest user3 = NewUserRequest.builder().name(USER_NAME_3).email(USER_EMAIL_3).login(USER_LOGIN_3)
                .birthday(USER_BIRTHDAY_3)
                .build();
        int userId3 = Objects.requireNonNull(userController.addUser(user3).getBody()).getId();

        //when
        userController.addFriend(userId, userId2);
        userController.addFriend(userId2, userId3);
        List<UserDto> commonFriends = userController.getCommonFriends(userId, userId2).getBody();

        //then
        assertNotNull(commonFriends, "Список общих друзей не проинициализировался");
        assertEquals(0, commonFriends.size(), "Список общих друзей не пустой");
    }
}