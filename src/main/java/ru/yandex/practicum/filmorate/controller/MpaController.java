package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    /**
     * Возвращает рейтинг MPA по id
     */
    @GetMapping("/{id}")
    public ResponseEntity<MpaDto> getMpaById(@PathVariable int id) {
        return ResponseEntity.ok(mpaService.getMpaById(id));
    }

    /**
     * Возвращает все рейтинги MPA
     */
    @GetMapping
    public ResponseEntity<List<MpaDto>> getMpaList() {
        return ResponseEntity.ok(mpaService.getMpaList());
    }
}