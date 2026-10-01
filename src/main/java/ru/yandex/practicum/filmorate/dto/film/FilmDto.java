package ru.yandex.practicum.filmorate.dto.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaaRatingDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;

    private MpaaRatingDto mpa;
    private List<GenreDto> genres;

    public FilmDto(Film film) {
        this.id = film.getId();
        this.name = film.getName();
        this.description = film.getDescription();
        this.releaseDate = film.getReleaseDate();
        this.duration = film.getDuration();

        if (film.getMpa() != null) {
            this.mpa = new MpaaRatingDto(film.getMpa());
        }

        if (film.getGenres() != null) {
            this.genres = film.getGenres().stream()
                    .map(GenreDto::new)
                    .collect(Collectors.toList());
        }
    }
}