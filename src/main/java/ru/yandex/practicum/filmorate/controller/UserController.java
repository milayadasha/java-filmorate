package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();

    /**
     * Возвращает всех пользователей в виде списка
     */
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    /**
     * Добавляет нового пользователя.
     * Проверяет поля пользователя на соответствие.
     * Если всё хорошо, то создаёт копию переданного пользователя, присваивает уникальный ID и сохраняет в контроллер.
     *
     * @param newUser объект пользователя, который нужно добавить
     * @return копия созданного пользователя с присвоенным ID
     */
    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User newUser) {
        try {
            checkIsValidUser(newUser);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных пользователя при добавлении: {}",
                    exception.getMessage());
            throw exception;
        }
        log.info("Данные пользователя прошли валидацию при добавлении");

        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} добавлен", newUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    /**
     * Обновляет пользователя.
     * Проверяет поля переданного пользователя на соответствие.
     * Если всё хорошо и переданный пользователь существует в контроллере, то создаёт копию и сохраняет в контроллер.
     *
     * @param updatedUser объект пользователя, который нужно обновить
     * @return копия обновлённого пользователя
     */
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updatedUser) {
        try {
            checkIsValidUser(updatedUser);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации данных пользователя {} при обновлении: {}", updatedUser.getId(),
                    exception.getMessage());
            throw exception;
        }
        log.info("Данные пользователя {} прошли валидацию при обновлении", updatedUser.getId());

        if (!users.containsKey(updatedUser.getId())) {
            String userNotFound = "Пользователь " + updatedUser.getId() + " для обновления не найден";
            log.error(userNotFound);
            throw new ValidationException(userNotFound);
        }

        users.put(updatedUser.getId(), updatedUser);
        log.info("Пользователь {} обновлён", updatedUser.getId());

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Генерирует следующий Id.
     * Находит максимальный текущй Id и увеличивает его.
     */
    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    /**
     * Проверяет переданного пользователя на соответствие условиям.
     * Если не удовлетворяет какой-то проверке, то выбрасывается ошибка
     */
    private void checkIsValidUser(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Пользователь не найден");
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