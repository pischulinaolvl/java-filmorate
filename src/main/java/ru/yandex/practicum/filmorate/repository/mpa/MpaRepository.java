package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.util.List;

public interface MpaRepository {
    MpaaRating getMpaById(Long id);

    List<MpaaRating> getAllMpa();

    MpaaRating createMpa(MpaaRating mpa);

    MpaaRating updateMpa(MpaaRating mpa);

    void deleteMpa(Long id);
}