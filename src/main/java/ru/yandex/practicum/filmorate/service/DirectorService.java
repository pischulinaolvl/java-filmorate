package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorRepository directorRepository;

    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public Director createDirector(Director director) {
        return directorRepository.createDirector(director);
    }

    public Director getDirectorById(Long id) {
        return directorRepository.findDirectorById(id);
    }

    public List<Director> getAllDirectors() {
        return directorRepository.getDirectors();
    }

    public Director updateDirector(Director director) {
        return directorRepository.updateDirector(director);
    }

    public void deleteDirector(Long id) {
        directorRepository.deleteDirector(id);
    }
}