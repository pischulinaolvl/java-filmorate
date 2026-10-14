package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.stream.Collectors;

public final class DirectorMapper {
    public static DirectorDto mapToDirectorDto(Director director) {
        if (director == null) {
            return null;
        }

        DirectorDto dto = new DirectorDto();

        dto.setId(director.getId());
        dto.setName(director.getName());

        return dto;
    }

    public static Director mapToDirector(DirectorDto dto) {
        if (dto == null) {
            return null;
        }

        Director director = new Director();

        director.setId(dto.getId());
        director.setName(dto.getName());

        return director;
    }

    public static List<DirectorDto> mapToDirectorDtoList(List<Director> directors) {
        if (directors == null) {
            return List.of();
        }

        return directors.stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }
}