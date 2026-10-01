package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.MpaaRating;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MpaaRatingDto {
    private Long id;
    private String name;
    private String description;

    public MpaaRatingDto(MpaaRating mpaRating) {
        if (mpaRating != null) {
            this.id = mpaRating.getId();
            this.name = mpaRating.getName();
            this.description = mpaRating.getDescription();
        }
    }
}
