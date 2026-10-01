package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.genre.GenreMapper;
import ru.yandex.practicum.filmorate.repository.mpa.MpaMapper;

import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {
    public static FilmDto mapToFilmDto(Film film) {
        if (film == null) {
            return null;
        }

        FilmDto dto = new FilmDto();

        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            dto.setMpa(MpaMapper.mapToMpaDto(film.getMpa()));
        } else {
            dto.setMpa(null);
        }

        if (film.getGenres() != null) {
            dto.setGenres(
                    film.getGenres().stream()
                            .map(GenreMapper::mapToGenreDto)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setGenres(List.of());
        }

        return dto;
    }

    public static List<FilmDto> mapToFilmDtoList(List<Film> films) {
        if (films == null) {
            return List.of();
        }
        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public static Film mapToFilm(NewFilmRequest request) {
        if (request == null) {
            return null;
        }

        Film film = new Film();;

        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (request.getMpa() != null) {
            film.setMpa(MpaMapper.mapToMpa(request.getMpa()));
        } else {
            film.setMpa(null);
        }

        if (request.getGenres() != null) {
            film.setGenres(
                    request.getGenres().stream()
                            .map(GenreMapper::mapToGenre)
                            .collect(Collectors.toList())
            );
        } else {
            film.setGenres(List.of());
        }
        return film;
    }

    public static Film mapToFilm(FilmDto filmDto) {
        if (filmDto == null) {
            return null;
        }

        Film film = new Film();;

        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());

        if (filmDto.getMpa() != null) {
            film.setMpa(MpaMapper.mapToMpa(filmDto.getMpa()));
        } else {
            film.setMpa(null);
        }

        if (filmDto.getGenres() != null) {
            film.setGenres(
                    filmDto.getGenres().stream()
                            .map(GenreMapper::mapToGenre)
                            .collect(Collectors.toList())
            );
        } else {
            film.setGenres(List.of());
        }
        return film;
    }

}
