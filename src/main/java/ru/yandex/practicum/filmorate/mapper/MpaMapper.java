package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MpaMapper {
    public static Mpa mapToMpa(MpaDto request) {
        return new Mpa(request.getId(), request.getName());
    }

    public static MpaDto mapToMpaDto(Mpa mpa) {
        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }
}
