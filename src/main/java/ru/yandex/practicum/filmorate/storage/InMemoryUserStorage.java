package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final HashMap<Integer, User> users = new HashMap<>();

    /**
     * Возвращает пользователя по id
     */
    @Override
    public User getUserById(int id) {
        if (users.get(id) == null) {
            String userNotFound = "Пользователь с id = " + id + " не найден";
            log.error(userNotFound);
            throw new NotFoundException(userNotFound);
        }

        return users.get(id);
    }

    /**
     * Возвращает всех пользователей в виде списка
     */
    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Добавляет нового пользователя.
     * Присваивает уникальный ID и сохраняет в набор пользователей.
     *
     * @param newUser объект пользователя, который нужно добавить
     * @return созданный пользователь с присвоенным ID
     */
    @Override
    public User addUser(User newUser) {
        if (newUser == null) {
            String userNotFound = "Пользователь для добавления не найден";
            log.error(userNotFound);
            throw new NotFoundException(userNotFound);
        }

        newUser.setId(getNextId());
        log.info("Пользователю с логином {} присвоен id {}", newUser.getLogin(), newUser.getId());

        if (newUser.getFriends() == null) {
            newUser.setFriends(new HashSet<>());
        }

        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} добавлен в хранилище", newUser.getId());
        return newUser;
    }

    /**
     * Обновляет пользователя.
     * Если переданный пользователь существует, то обновляет пользователя в наборе пользователей.
     *
     * @param updatedUser объект пользователя, который нужно обновить
     * @return обновлённый пользователь
     */
    @Override
    public User updateUser(User updatedUser) {
        if (updatedUser == null) {
            String userNotFound = "Пользователь для обновления не найден";
            log.error(userNotFound);
            throw new NotFoundException(userNotFound);
        }

        if (!users.containsKey(updatedUser.getId())) {
            String userNotFound = "Пользователь " + updatedUser.getId() + " для обновления не найден в хранилище";
            log.error(userNotFound);
            throw new NotFoundException(userNotFound);
        }

        if (updatedUser.getFriends() == null) {
            updatedUser.setFriends(new HashSet<>());
        }

        users.put(updatedUser.getId(), updatedUser);
        log.info("Пользователь {} обновлён", updatedUser.getId());

        return updatedUser;
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
}