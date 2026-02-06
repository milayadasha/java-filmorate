package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.FilmRowMapper;

import java.util.*;

@Primary
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private static final String GET_FILM_BY_ID = "SELECT * FROM films WHERE id = ?;";
    private static final String GET_FILMS = "SELECT * FROM films;";
    private static final String ADD_FILM = "INSERT INTO films (name, description, duration, release_date, " +
            "mpa_rating_id) VALUES (?, ?, ?, ?, ?);";
    private static final String ADD_FILM_SIMPLE = "INSERT INTO films (name, description, duration, release_date) " +
            "VALUES (?, ?, ?, ?);";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?,description = ?, duration = ?, " +
            "release_date = ?, mpa_rating_id = ? WHERE id = ?;";
    private static final String UPDATE_FILM_SHORT = "UPDATE films SET name = ?,description = ?, duration = ?, " +
            "release_date = ? WHERE id = ?;";
    private static final String GET_POPULAR_FILMS = "SELECT f.*, COUNT(fl.user_id) as likes_count FROM films f " +
            "LEFT JOIN films_likes fl ON f.id = fl.film_id " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?;";

    private static final String GET_GENRES_ID = "SELECT genre_id FROM films_genres WHERE film_id = ?;";
    private static final String GET_GENRE_NAME = "SELECT name FROM genres WHERE id = ?;";
    private static final String ADD_GENRE = "INSERT INTO films_genres (film_id, genre_id) " +
            "VALUES (?, ?);";
    private static final String DELETE_GENRES = "DELETE FROM films_genres WHERE film_id = ?;";

    private static final String GET_MPA_ID = "SELECT mpa_rating_id FROM films WHERE id = ?;";
    private static final String GET_MPA_NAME = "SELECT name FROM mpa_ratings WHERE id = ?;";

    private static final String ADD_USER_LIKE = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);";
    private static final String DELETE_USER_LIKE = "DELETE FROM films_likes WHERE film_id = ? " +
            "AND user_id = ?;";


    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, MpaStorage mpaStorage, GenreStorage genreStorage) {
        super(jdbc, mapper);
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    /**
     * Возвращает фильм по id из БД
     */
    @Override
    public Film getFilmById(int id) {
        Film film = findOne(GET_FILM_BY_ID, id).orElseThrow(() -> {
            String filmNotFound = "Фильм с id = " + id + " не найден";
            log.error(filmNotFound);
            return new NotFoundException(filmNotFound);
        });
        enrichFilmByAdditionalInfo(film);
        return film;
    }

    /**
     * Возвращает все фильмы из БД в виде списка
     */
    @Override
    public List<Film> getFilms() {
        return findMany(GET_FILMS).stream().peek(this::enrichFilmByAdditionalInfo).toList();
    }

    /**
     * Добавляет новый фильм в БД
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
        checkFilmMpaValid(newFilm);
        checkFilmGenresValid(newFilm);

        int id;
        if (newFilm.getMpa() == null) {
            id = insert(ADD_FILM_SIMPLE, newFilm.getName(), newFilm.getDescription(), newFilm.getDuration(),
                    newFilm.getReleaseDate());
        } else {
            id = insert(ADD_FILM, newFilm.getName(), newFilm.getDescription(), newFilm.getDuration(),
                    newFilm.getReleaseDate(), newFilm.getMpa().getId());
        }
        newFilm.setId(id);

        log.info("Фильму {} присвоен id {}", newFilm.getName(), newFilm.getId());
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            newFilm.getGenres().forEach(genre -> insert(ADD_GENRE, newFilm.getId(), genre.getId()));
        }

        enrichFilmByAdditionalInfo(newFilm);
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
        try {
            checkFilmMpaValid(updatedFilm);
            checkFilmGenresValid(updatedFilm);

            if (updatedFilm.getMpa() == null) {
                update(UPDATE_FILM_SHORT, updatedFilm.getName(), updatedFilm.getDescription(), updatedFilm.getDuration(),
                        updatedFilm.getReleaseDate(), updatedFilm.getId());
            } else {
                update(UPDATE_FILM, updatedFilm.getName(), updatedFilm.getDescription(), updatedFilm.getDuration(),
                        updatedFilm.getReleaseDate(), updatedFilm.getMpa().getId(), updatedFilm.getId());
            }

            if (updatedFilm.getGenres() != null && !updatedFilm.getGenres().isEmpty()) {
                update(DELETE_GENRES, updatedFilm.getId());
                updatedFilm.getGenres().forEach(genre ->
                        update(ADD_GENRE, updatedFilm.getId(), genre.getId()));
            }
            enrichFilmByAdditionalInfo(updatedFilm);
            log.info("Фильм {} обновлён в хранилище", updatedFilm.getId());
            return updatedFilm;
        } catch (InternalServerException exception) {
            log.error("Фильм {} не обновлён из-за внутренней ошибки", updatedFilm.getId());
            throw exception;
        }
    }

    /**
     * Добавляет лайк от пользователя.
     * Делает запись в БД.
     */
    public void addLike(int filmId, int userId) {
        try {
            update(ADD_USER_LIKE, filmId, userId);
        } catch (InternalServerException exception) {
            log.error("Не удалось добавить лайк для фильма {} от пользователя {}", filmId, userId);
            throw exception;
        }
    }

    /**
     * Удаляет лайк от пользователя.
     * Делает запись в БД.
     */
    public void removeLike(int filmId, int userId) {
        try {
            update(DELETE_USER_LIKE, filmId, userId);
        } catch (InternalServerException exception) {
            log.error("Не удалось удалить лайк для фильма {} от пользователя {}", filmId, userId);
            throw exception;
        }
    }

    /**
     * Возвращает ТОП фильмов по числу лайков
     */
    public List<Film> getMostPopularFilms(int count) {
        return findMany(GET_POPULAR_FILMS, count).stream().peek(this::enrichFilmByAdditionalInfo).toList();
    }

    /**
     * Обогащает фильм дополнительной информацией.
     */
    private Film enrichFilmByAdditionalInfo(Film film) {
        if (film == null) {
            String filmNotFound = "Фильм для обогащения не найден";
            log.error(filmNotFound);
            throw new NotFoundException(filmNotFound);
        }
        film.setGenres(new LinkedHashSet<>(getFilmGenres(film.getId())));
        film.setMpa(getFilmMpaRating(film.getId()));

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        return film;
    }

    /**
     * Формирует список жанров фильма
     */
    private List<Genre> getFilmGenres(int filmId) {
        List<Integer> genreIdList = jdbc.queryForList(GET_GENRES_ID, Integer.class, filmId);

        if (genreIdList.isEmpty()) {
            return new ArrayList<>();
        }

        return genreIdList.stream()
                .map(genreId -> {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    return genre;
                }).peek(genre -> {
                    String genreName = jdbc.queryForObject(GET_GENRE_NAME, String.class, genre.getId());
                    genre.setName(genreName);
                })
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
    }

    /**
     * Формирует объект MPA рейтинга
     */
    private Mpa getFilmMpaRating(int filmId) {
        Integer mpaRatingId = jdbc.queryForObject(GET_MPA_ID, Integer.class, filmId);
        if (mpaRatingId == null) {
            return null;
        }
        String mpaName = jdbc.queryForObject(GET_MPA_NAME, String.class, mpaRatingId);
        return new Mpa(mpaRatingId, mpaName);
    }

    /**
     * Проверяет, что MPA рейтинг фильма существует
     */
    private void checkFilmMpaValid(Film film) {
        Mpa filmMpa = film.getMpa();
        if (filmMpa == null) {
            return;
        }
        if (!mpaStorage.getMpaList().contains(filmMpa)) {
            throw new NotFoundException("MPA рейтинг " + filmMpa.getId() + " не найден");
        }
    }

    /**
     * Проверяет, что каждый жанр фильма существует
     */
    private void checkFilmGenresValid(Film film) {
        Set<Genre> filmGenres = film.getGenres();
        if (filmGenres == null || filmGenres.isEmpty()) {
            return;
        }
        filmGenres.forEach(genre -> {
            if (!genreStorage.getGenres().contains(genre)) {
                throw new NotFoundException("MPA рейтинг " + genre.getId() + " не найден");
            }
        });
    }
}