package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class UserDbStorageTest {
    private static final String USER_NAME = "Вася";
    private static final String USER_NAME_2 = "Петя";

    private static final String USER_EMAIL = "mail@mail.ru";
    private static final String USER_EMAIL_2 = "newMail@mail.ru";

    private static final String USER_LOGIN = "user";
    private static final String USER_LOGIN_2 = "newUser";

    private static final LocalDate USER_BIRTHDAY = LocalDate.of(1992, Month.DECEMBER, 12);
    private static final LocalDate USER_BIRTHDAY_2 = LocalDate.of(1982, Month.OCTOBER, 22);

    private static final Integer USER_ID = 567;
    private static final String DELETE_USERS = "DELETE FROM users;";

    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    @DisplayName("Чистим БД")
    void cleanDatabase() {
        jdbcTemplate.update(DELETE_USERS);
    }

    @Test
    @DisplayName("При получении существующего пользователя по id хранилище должно вернуть его")
    void test_getUserById_WhenUserExists_ShouldReturnOne() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User createdUser = userStorage.addUser(user);

        //when
        User findUser = userStorage.getUserById(createdUser.getId());

        //then
        assertNotNull(findUser, "В БД нет пользователей");
        assertEquals(USER_NAME, findUser.getName(), "В БД некорректный пользователь");
    }

    @Test
    @DisplayName("При получении несуществующего пользователя по id хранилище должно выбросить ошибку")
    void test_getUserById_WhenUserNotExists_ShouldThrowsError() {
        //given && when && then
        assertThrows(NotFoundException.class,
                () -> userStorage.getUserById(USER_ID),
                "Из контроллера получен несуществующий пользователь");
    }

    @Test
    @DisplayName("При добавлении двух пользователей хранилище должно возвращать их")
    void test_getUsers_WhenAddedUsers_ShouldReturnAll() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User user2 = User.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2).birthday(USER_BIRTHDAY_2)
                .build();

        //when
        userStorage.addUser(user);
        userStorage.addUser(user2);
        List<User> usersByController = userStorage.getUsers();

        //then
        assertNotNull(usersByController, "В БД нет пользователей");
        assertEquals(2, usersByController.size(), "В БД не верное количество пользователей");
        assertEquals(USER_NAME, usersByController.get(0).getName(),
                "В БД не корректный 1-ый пользователь");
        assertEquals(USER_NAME_2, usersByController.get(1).getName(),
                "В БД не корректный 2-ой пользователь");
    }

    @Test
    @DisplayName("При добавлении пользователя с корректными полями хранилище должно добавить его")
    void test_addUser_WhenCorrectFields_ShouldAddToStorage() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();

        //when
        userStorage.addUser(user);
        List<User> usersByController = userStorage.getUsers();

        //then
        assertNotNull(usersByController, "В БД нет пользователей");
        assertEquals(1, usersByController.size(), "В БД не верное количество пользователей");
        assertEquals(USER_NAME, usersByController.get(0).getName(), "В БД некорректный пользователь");
    }

    @Test
    @DisplayName("При добавлении в друзья существующих пользователей список друзей должен обновиться")
    void test_addFriends_WhenUsersExist_ShouldReturnOne() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User user2 = User.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2).birthday(USER_BIRTHDAY_2)
                .build();
        User createdUser1 = userStorage.addUser(user);
        User createdUser2 = userStorage.addUser(user2);

        //when
        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());
        List<Integer> friendsId = new ArrayList<>(userStorage.getUserFriends(createdUser1.getId()));

        //then
        assertEquals(1, friendsId.size(),
                "При добавлении в друзья список друзей остался пуст");
    }

    @Test
    @DisplayName("При удалении из друзей пользователя список друзей должен обновиться")
    void test_removeFriends_WhenFriendsExist_ShouldReturnZero() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User user2 = User.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2).birthday(USER_BIRTHDAY_2)
                .build();
        User createdUser1 = userStorage.addUser(user);
        User createdUser2 = userStorage.addUser(user2);
        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());

        //when
        userStorage.removeFriends(createdUser1.getId(), createdUser2.getId());
        List<Integer> friendsId = new ArrayList<>(userStorage.getUserFriends(createdUser1.getId()));

        //then
        assertEquals(0, friendsId.size(),
                "При удалении из друзей пользователь остался в друзьях");
    }

    @Test
    @DisplayName("При получении всех друзей пользователя должен возвращаться корректный список")
    void test_getUserFriends_WhenAddFriends_ShouldReturnOne() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User user2 = User.builder().name(USER_NAME_2).email(USER_EMAIL_2).login(USER_LOGIN_2).birthday(USER_BIRTHDAY_2)
                .build();
        User createdUser1 = userStorage.addUser(user);
        User createdUser2 = userStorage.addUser(user2);

        //when
        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());
        List<Integer> friendsId = new ArrayList<>(userStorage.getUserFriends(createdUser1.getId()));

        //then
        assertEquals(1, friendsId.size(),
                "При добавлении в друзья список друзей остался пуст");
    }

    @Test
    @DisplayName("При отсутствии друзей у пользователя должен возвращаться пустой список")
    void test_getUserFriends_WhenNoFriends_ShouldReturnEmpty() {
        //given
        User user = User.builder().name(USER_NAME).email(USER_EMAIL).login(USER_LOGIN).birthday(USER_BIRTHDAY).build();
        User createdUser1 = userStorage.addUser(user);

        //when
        List<Integer> userFriends = new ArrayList<>(userStorage.getUserFriends(createdUser1.getId()));

        //then
        assertNotNull(userFriends, "Список друзей не проинициализировался");
        assertEquals(0, userFriends.size(), "Список друзей не пустой");
    }
}