package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Возвращает пользователя по id.
     * Вызывает метод хранилища по получению пользователя по id.
     */
    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    /**
     * Возвращает всех пользователей в виде списка.
     * Вызывает соответствующий метод хранилища.
     */
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    /**
     * Добавляет нового пользователя.
     * Проверяет поля пользователя на соответствие.
     * Если всё хорошо, то вызывает соответствующий метод хранилища.
     *
     * @param newUser объект пользователя, который нужно добавить
     * @return созданный пользователь с присвоенным ID
     */
    public User addUser(User newUser) {
        try {
            checkIsValidUser(newUser);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных пользователя при добавлении: {}",
                    exception.getMessage());
            throw exception;
        }
        log.trace("Данные новго пользователя c логином {} прошли валидацию при добавлении", newUser.getLogin());
        return userStorage.addUser(newUser);
    }

    /**
     * Обновляет пользователя.
     * Проверяет поля переданного пользователя на соответствие.
     * Если всё хорошо, то вызывает соответствующий метод хранилища.
     *
     * @param updatedUser объект пользователя, который нужно обновить
     * @return обновлённый пользователь
     */
    public User updateUser(User updatedUser) {
        try {
            checkIsValidUser(updatedUser);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных пользователя {} при обновлении: {}", updatedUser.getId(),
                    exception.getMessage());
            throw exception;
        }
        log.info("Данные пользователя {} прошли валидацию при обновлении", updatedUser.getId());

        return userStorage.updateUser(updatedUser);
    }

    /**
     * Добавляет пользователей в друзья.
     * Если оба пользователя существуют, то обновляет их списки друзей и обновляет пользователей в хранилище.
     */
    public void addFriends(int firstUserId, int secondUserId) {
        User firstUser = userStorage.getUserById(firstUserId);
        log.trace("Пользователь {} найден для добавления в друзья", firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);
        log.trace("Пользователь {} найден для добавления в друзья", secondUserId);

        firstUser.getFriends().add(secondUserId);
        secondUser.getFriends().add(firstUser.getId());
        log.info("Пользователь {} добавлен в друзья пользователя {}.",
                secondUserId, firstUserId);

        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        log.trace("Пользователи обновлены в хранилище.");
    }

    /**
     * Удаляет пользователей из друзей.
     * Если оба пользователя существуют, то обновляет их списки друзей и передаёт обновлённых пользователей в мапу.
     */
    public void removeFriends(int firstUserId, int secondUserId) {
        User firstUser = userStorage.getUserById(firstUserId);
        log.trace("Пользователь {} найден для удаления мз друзей", firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);
        log.trace("Пользователь {} найден для удаления мз друзей", secondUserId);

        firstUser.getFriends().remove(secondUserId);
        secondUser.getFriends().remove(firstUserId);
        log.info("Пользователь {} удален из друзей пользователя {}.",
                secondUserId, firstUserId);

        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        log.trace("Пользователи обновлены в хранилище.");
    }

    /**
     * Ищет общих друзей у пользователей
     * Если оба пользователя существуют, то возвращает список общих друзей.
     */
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        List<User> firstFriends = getUserFriendsById(firstUserId);
        List<User> secondFriends = getUserFriendsById(secondUserId);
        log.trace("Списки друзей пользователей для поиска общих друзей составлены", secondUserId);

        return firstFriends.stream()
                .filter(secondFriends::contains)
                .toList();
    }

    /**
     * Возвращает всех друзей пользователя
     * Если пользователь существуюет, то возвращает список по id
     */
    public List<User> getUserFriendsById(int userId) {
        User user = userStorage.getUserById(userId);
        log.trace("Пользователь {} найден для поиска всех его друзей", userId);

        return user.getFriends().stream().map(id -> userStorage.getUserById(id)).toList();
    }

    /**
     * Проверяет переданного пользователя на соответствие условиям.
     * Если не удовлетворяет какой-то проверке, то выбрасывается ошибка
     */
    private void checkIsValidUser(User user) throws ValidationException {
        if (user == null) {
            throw new NotFoundException("Пользователь для валидации входных параметров не найден");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин пользователя не может содержать пробел");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
