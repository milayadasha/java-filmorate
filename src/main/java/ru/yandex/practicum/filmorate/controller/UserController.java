package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Возвращает пользователя по id
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Возвращает всех пользователей в виде списка
     */
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Добавляет нового пользователя.
     *
     * @param newUser объект пользователя, который нужно добавить
     * @return ответ, содержащий созданного пользователя с присвоенным ID
     */
    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User newUser) {
        User createdUser = userService.addUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Обновляет пользователя.
     *
     * @param updatedUser объект пользователя, который нужно обновить
     * @return ответ, содержащий обновлённого пользователя
     */
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updatedUser) {
        User savedUser = userService.updateUser(updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Добавляет пользователя в друзья к другому пользователю
     *
     * @param id       идентификатор пользователя, которому нужно добавить друга.
     * @param friendId идентификатор пользователя, которого нужно добавить в друзья
     * @return пустой ответ со статусом 200
     */
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriends(id, friendId);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаляет пользователя из друзей другого пользователя
     *
     * @param id       идентификатор пользователя, у которого нужно удалить друга.
     * @param friendId идентификатор пользователя, которого нужно удалить из друзей
     * @return пустой ответ со статусом 200
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriends(id, friendId);
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает друзей пользователя по его id.
     *
     * @param id идентификатор пользователя.
     * @return список друзей-пользователей указанного пользователя.
     */
    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getAllFriends(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserFriendsById(id));
    }

    /**
     * Возвращает общих друзей пользователей по их id.
     *
     * @param id      идентификатор пользователя, для которого ищем общих друзей.
     * @param otherId идентификатор пользователя, с кем ищем общих друзей.
     * @return список общих друзей в виде списка пользователей.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
    }
}