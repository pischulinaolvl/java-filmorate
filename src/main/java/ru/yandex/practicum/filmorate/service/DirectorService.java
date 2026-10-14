package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorMapper;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorRepository directorRepository;

    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public DirectorDto createDirector(DirectorDto directorDto) {
        return DirectorMapper.mapToDirectorDto(directorRepository.createDirector(DirectorMapper.mapToDirector(directorDto)));
    }

    public DirectorDto getDirectorById(Long id) {
        return DirectorMapper.mapToDirectorDto(directorRepository.findDirectorById(id));
    }

    public List<DirectorDto> getAllDirectors() {
        return DirectorMapper.mapToDirectorDtoList(directorRepository.getDirectors());
    }

    public DirectorDto updateDirector(DirectorDto directorDto) {
        return DirectorMapper.mapToDirectorDto(directorRepository.updateDirector(DirectorMapper.mapToDirector(directorDto)));
    }

    public void deleteDirector(Long id) {
        directorRepository.deleteDirector(id);
    }
}