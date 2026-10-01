package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaRepository mpaStorage;

    public MpaaRating getMpaById(Long id) {
        return mpaStorage.getMpaById(id);
    }

    public List<MpaaRating> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public MpaaRating createMpa(MpaaRating mpa) {
        return mpaStorage.createMpa(mpa);
    }

    public MpaaRating updateMpa(MpaaRating mpa) {
        return mpaStorage.updateMpa(mpa);
    }

    public void deleteMpa(Long id) {
        mpaStorage.deleteMpa(id);
    }
}