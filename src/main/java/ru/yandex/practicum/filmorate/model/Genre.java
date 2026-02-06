package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    private Integer id;
    private String name;
}
