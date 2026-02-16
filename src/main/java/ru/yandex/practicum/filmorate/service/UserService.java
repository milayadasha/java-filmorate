package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

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
    public UserDto getUserById(int id) {
        return UserMapper.mapToUserDto(userStorage.getUserById(id));
    }

    /**
     * Возвращает всех пользователей в виде списка.
     * Вызывает соответствующий метод хранилища.
     */
    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream().map(UserMapper::mapToUserDto).toList();
    }

    /**
     * Добавляет нового пользователя.
     * Проверяет поля пользователя на соответствие.
     * Если всё хорошо, то вызывает соответствующий метод хранилища.
     *
     * @param newUser объект пользователя, который нужно добавить
     * @return созданный пользователь с присвоенным ID
     */
    public UserDto addUser(NewUserRequest newUserDto) {
        User newUser = UserMapper.mapToUser(newUserDto);
        try {
            checkIsValidUser(newUser);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных пользователя при добавлении: {}",
                    exception.getMessage());
            throw exception;
        }
        log.trace("Данные новго пользователя c логином {} прошли валидацию при добавлении", newUser.getLogin());
        return UserMapper.mapToUserDto(userStorage.addUser(newUser));
    }

    /**
     * Обновляет пользователя.
     * Проверяет поля переданного пользователя на соответствие.
     * Если всё хорошо, то вызывает соответствующий метод хранилища.
     *
     * @param updatedUser объект пользователя, который нужно обновить
     * @return обновлённый пользователь
     */
    public UserDto updateUser(UpdateUserRequest updatedUserDto) {
        User updatedUser = userStorage.getUserById(updatedUserDto.getId());
        updatedUser = UserMapper.updateUserFields(updatedUser, updatedUserDto);

        try {
            checkIsValidUser(updatedUser);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных пользователя {} при обновлении: {}", updatedUser.getId(),
                    exception.getMessage());
            throw exception;
        }
        log.info("Данные пользователя {} прошли валидацию при обновлении", updatedUser.getId());

        return UserMapper.mapToUserDto(userStorage.updateUser(updatedUser));
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

        if (firstUser.getFriends().contains(secondUserId)) {
            throw new ValidationException("Пользователь уже добавлен в друзья");
        }

        userStorage.addFriends(firstUserId, secondUserId);
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

        userStorage.removeFriends(firstUserId, secondUserId);
        log.trace("Пользователи обновлены в хранилище.");
    }

    /**
     * Ищет общих друзей у пользователей
     * Если оба пользователя существуют, то возвращает список общих друзей.
     */
    public List<UserDto> getCommonFriends(int firstUserId, int secondUserId) {
        List<UserDto> firstFriends = getUserFriendsById(firstUserId);
        List<UserDto> secondFriends = getUserFriendsById(secondUserId);
        log.trace("Списки друзей пользователей для поиска общих друзей составлены", secondUserId);

        return firstFriends.stream()
                .filter(secondFriends::contains)
                .toList();
    }

    /**
     * Возвращает всех друзей пользователя
     * Если пользователь существуюет, то возвращает список по id
     */
    public List<UserDto> getUserFriendsById(int userId) {
        User user = userStorage.getUserById(userId);
        log.trace("Пользователь {} найден для поиска всех его друзей", userId);

        return user.getFriends().stream()
                .map(id -> userStorage.getUserById(id))
                .map(UserMapper::mapToUserDto)
                .toList();
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
