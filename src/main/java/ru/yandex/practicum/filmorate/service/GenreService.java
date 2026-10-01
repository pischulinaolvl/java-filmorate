package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreMapper;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreDto getGenreById(Long id) {
        Genre genre = genreRepository.getGenreById(id);
        return GenreMapper.mapToGenreDto(genre);
    }

    public List<GenreDto> getAllGenres() {
        List<Genre> genres = genreRepository.getAllGenres();
        return genres.stream()
                .map(entry -> {
                    return new GenreDto(entry.getId(), entry.getName());
                })
                .collect(Collectors.toList());
    }

    public GenreDto createGenre(GenreDto genreDto) {
        Genre genre = GenreMapper.mapToGenre(genreDto);

        Genre savedGenre = genreRepository.createGenre(genre);

        return GenreMapper.mapToGenreDto(savedGenre);
    }

    public GenreDto updateGenre(GenreDto genreDto) {
        Genre genre = GenreMapper.mapToGenre(genreDto);
        Genre updatedGenre = genreRepository.updateGenre(genre);
        return GenreMapper.mapToGenreDto(updatedGenre);
    }

    public void deleteGenre(Long id) {
        genreRepository.deleteGenre(id);
    }
}