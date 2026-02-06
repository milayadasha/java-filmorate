package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private int id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    private Integer duration;

    private Set<Integer> likes;
    private Set<Genre> genres;
    private Mpa mpa;
}