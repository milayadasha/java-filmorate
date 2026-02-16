package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    /**
     * Возвращает жанр по id.
     */
    public GenreDto getGenreById(int id) {
        return GenreMapper.mapToGenreDto(genreStorage.getGenreById(id));
    }

    /**
     * Возвращает всех жанры в виде списка.
     */
    public List<GenreDto> getGenres() {
        return genreStorage.getGenres().stream().map(GenreMapper::mapToGenreDto).toList();
    }
}