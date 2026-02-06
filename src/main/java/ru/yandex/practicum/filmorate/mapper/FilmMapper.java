package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        String name = request.getName();
        String description = request.getDescription();
        LocalDate releaseDate = request.getReleaseDate();
        Integer duration = request.getDuration();
        Set<Genre> genres = request.getGenres();
        Mpa mpaRatingId = request.getMpa();

        return Film.builder()
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .genres(genres)
                .mpa(mpaRatingId)
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setGenres(film.getGenres());
        dto.setMpa(film.getMpa());
        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }

        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }

        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }

        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }

        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }

        if (request.hasMpaRating()) {
            film.setMpa(request.getMpa());
        }
        return film;
    }
}