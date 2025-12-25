package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    /**
     * Возвращает фильм по id
     */
    @Override
    public Film getFilmById(int id) {
        if (films.get(id) == null) {
            String filmNotFound = "Фильм по указанному id " + id + " не найден.";
            log.error(filmNotFound);
            throw new NotFoundException(filmNotFound);
        }
        return films.get(id);
    }

    /**
     * Возвращает все фильмы в виде списка
     */
    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    /**
     * Добавляет новый фильм.
     * Создаёт копию переданного фильма, присваивает уникальный ID и сохраняет в набор фильмов.
     *
     * @param newFilm объект фильма, который нужно добавить
     * @return созданный фильм с присвоенным ID
     */
    @Override
    public Film addFilm(Film newFilm) {
        if (newFilm == null) {
            String filmNotFound = "Фильм для добавления не найден";
            log.error(filmNotFound);
            throw new NotFoundException(filmNotFound);
        }

        newFilm.setId(getNextId());
        log.info("Фильму {} присвоен id {}", newFilm.getName(), newFilm.getId());

        if (newFilm.getLikes() == null) {
            newFilm.setLikes(new HashSet<>());
        }

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм {} добавлен в хранилище", newFilm.getId());
        return newFilm;
    }

    /**
     * Обновляет фильм.
     * Проверяет поля переданного фильма на соответствие.
     * Если всё хорошо и переданный фильм существует в контроллере, то создаёт копию и сохраняет в контроллер.
     *
     * @param updatedFilm объект фильма, который нужно обновить
     * @return копия обновлённого фильма
     */
    @Override
    public Film updateFilm(Film updatedFilm) {
        if (updatedFilm == null) {
            String filmNotFound = "Фильм для обновления не найден";
            log.error(filmNotFound);
            throw new NotFoundException(filmNotFound);
        }

        if (!films.containsKey(updatedFilm.getId())) {
            String filmNotFoundInStorage = "Фильм " + updatedFilm.getId() + " для обновления не найден в хранилище";
            log.error(filmNotFoundInStorage);
            throw new NotFoundException(filmNotFoundInStorage);
        }

        if (updatedFilm.getLikes() == null) {
            updatedFilm.setLikes(new HashSet<>());
        }

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Фильм {} обновлён в хранилище", updatedFilm.getId());
        return updatedFilm;
    }

    /**
     * Генерирует следующий Id.
     * Находит максимальный текущй Id и увеличивает его.
     */
    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
