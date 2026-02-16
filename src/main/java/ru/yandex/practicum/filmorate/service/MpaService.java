package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    /**
     * Возвращает MPA рейтинг по id.
     */
    public MpaDto getMpaById(int id) {
        return MpaMapper.mapToMpaDto(mpaStorage.getMpaById(id));
    }

    /**
     * Возвращает всех MPA рейтинги в виде списка.
     */
    public List<MpaDto> getMpaList() {
        return mpaStorage.getMpaList().stream().map(MpaMapper::mapToMpaDto).toList();
    }
}