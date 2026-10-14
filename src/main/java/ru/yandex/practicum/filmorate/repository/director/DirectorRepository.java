package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorRepository {
    Director createDirector(Director director);

    void deleteDirector(Long id);

    Director updateDirector(Director director);

    Director findDirectorById(Long id);

    List<Director> getDirectors();
}
