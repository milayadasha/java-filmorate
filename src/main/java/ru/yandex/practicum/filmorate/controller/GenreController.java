package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Возвращает жанр по id
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable int id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    /**
     * Возвращает все жанры в виде списка
     */
    @GetMapping
    public ResponseEntity<List<GenreDto>> getGenres() {
        return ResponseEntity.ok(genreService.getGenres());
    }
}
