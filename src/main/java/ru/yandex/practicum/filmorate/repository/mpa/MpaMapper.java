package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.dto.mpa.MpaaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaaRating;

public class MpaMapper {
    public static MpaaRatingDto mapToMpaDto(MpaaRating mpa) {
        if (mpa == null) {
            return null;
        }
        MpaaRatingDto dto = new MpaaRatingDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        dto.setDescription(mpa.getDescription());
        return dto;
    }

    public static MpaaRating mapToMpa(MpaaRatingDto dto) {
        if (dto == null) {
            return null;
        }
        MpaaRating mpa = new MpaaRating();
        mpa.setId(dto.getId());
        mpa.setName(dto.getName());
        mpa.setDescription(dto.getDescription());
        return mpa;
    }
}
