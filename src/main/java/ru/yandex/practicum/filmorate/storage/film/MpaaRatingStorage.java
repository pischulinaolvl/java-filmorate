package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.util.List;
import java.util.Optional;

public interface MpaaRatingStorage {
    MpaaRating getMpaById(Long id);
    List<MpaaRating> getAllMpa();
    MpaaRating createMpa(MpaaRating mpa);
    MpaaRating updateMpa(MpaaRating mpa);
    void deleteMpa(Long id);
}