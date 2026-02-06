package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserRowMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Repository("userDbStorage")
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private static final String GET_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?;";
    private static final String GET_USERS_QUERY = "SELECT * FROM users;";
    private static final String ADD_USER_QUERY = "INSERT INTO users (name, email, login, birthday)" +
            "VALUES (?, ?, ?, ?);";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? " +
            "WHERE id = ?;";
    private static final String GET_USER_FRIENDS = "SELECT friend_id FROM users_friendship WHERE user_id = ?;";
    private static final String ADD_USER_FRIEND = "INSERT INTO users_friendship (user_id, friend_id) VALUES (?, ?);";
    private static final String DELETE_USER_FRIEND = "DELETE FROM users_friendship WHERE user_id = ? " +
            "AND friend_id = ?;";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    /**
     * Возвращает пользователя по id из БД
     */
    @Override
    public User getUserById(int id) {
        User user = findOne(GET_USER_BY_ID_QUERY, id).orElseThrow(() -> {
            String userNotFound = "Пользователь с id = " + id + " не найден";
            log.error(userNotFound);
            return new NotFoundException(userNotFound);
        });
        user.setFriends(getUserFriends(id));
        return user;
    }

    /**
     * Возвращает из БД всех пользователей в виде списка
     */
    @Override
    public List<User> getUsers() {
        try {
            return findMany(GET_USERS_QUERY);
        } catch (InternalServerException exception) {
            log.error("Пользователи не найдены из-за внутренней ошибки");
            throw exception;
        }
    }

    /**
     * Добавляет нового пользователя.
     * Присваивает уникальный ID и сохраняет в БД.
     *
     * @param newUser объект пользователя, который нужно добавить
     * @return созданный пользователь с присвоенным ID
     */
    @Override
    public User addUser(User newUser) {
        int id = insert(ADD_USER_QUERY, newUser.getName(), newUser.getEmail(), newUser.getLogin(),
                newUser.getBirthday());
        newUser.setId(id);
        log.info("Пользователю с логином {} присвоен id {}", newUser.getLogin(), newUser.getId());

        if (newUser.getFriends() == null) {
            newUser.setFriends(new HashSet<>());
        }

        log.info("Пользователь {} добавлен в базу", newUser.getId());
        return newUser;
    }

    /**
     * Обновляет пользователя в БД, если он существует.
     *
     * @param updatedUser объект пользователя, который нужно обновить
     * @return обновлённый пользователь
     */
    @Override
    public User updateUser(User updatedUser) {
        try {
            update(UPDATE_USER_QUERY, updatedUser.getName(), updatedUser.getEmail(), updatedUser.getLogin(),
                    updatedUser.getBirthday(), updatedUser.getId());

            if (updatedUser.getFriends() == null) {
                updatedUser.setFriends(new HashSet<>());
            }

            log.info("Пользователь {} обновлён", updatedUser.getId());
            return updatedUser;
        } catch (InternalServerException exception) {
            log.error("Пользователь {} не обновлён из-за внутренней ошибки", updatedUser.getId());
            throw exception;
        }
    }

    /**
     * Добавляет в друзья пользователя другому пользователю.
     * Делает запись в БД.
     */
    @Override
    public void addFriends(int userId, int friendId) {
        try {
            jdbc.update(ADD_USER_FRIEND, userId, friendId);
        } catch (InternalServerException exception) {
            log.error("Не удалось добавить пользователя {} в друзья {}", friendId, userId);
            throw exception;
        }
    }

    /**
     * Удаляет одного пользователя из друзей второго пользователя.
     * Делает запись в БД.
     */
    @Override
    public void removeFriends(int userId, int friendId) {
        try {
            jdbc.update(DELETE_USER_FRIEND, userId, friendId);
        } catch (InternalServerException exception) {
            log.error("Не удалось удалить пользователя {} из друзей {}", friendId, userId);
            throw exception;
        }
    }

    /**
     * Возвращает список друзей пользователя в виде списка id
     */
    @Override
    public Set<Integer> getUserFriends(Integer userId) {
        try {
            return new HashSet<>(jdbc.queryForList(GET_USER_FRIENDS, Integer.class, userId));
        } catch (InternalServerException exception) {
            String getFriendsError = "Не удалось получить друзей пользователя " + userId;
            log.error(getFriendsError);
            throw exception;
        }
    }
}