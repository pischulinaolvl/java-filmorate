package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.storage.film.MpaaRatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaaRatingStorage mpaStorage;

    /**
     * Получить рейтинг по ID.
     * Если не найден — выбрасываем NotFoundException, чтобы контроллер вернул 404.
     */
    public MpaaRating getMpaById(Long id) {
        return mpaStorage.getMpaById(id);
    }

    /**
     * Получить все рейтинги.
     */
    public List<MpaaRating> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    /**
     * Создать новый рейтинг.
     * Валидация уже внутри хранилища (или дублируется здесь для чистоты API).
     */
    public MpaaRating createMpa(MpaaRating mpa) {
        return mpaStorage.createMpa(mpa);
    }

    /**
     * Обновить существующий рейтинг.
     */
    public MpaaRating updateMpa(MpaaRating mpa) {
        return mpaStorage.updateMpa(mpa);
    }

    /**
     * Удалить рейтинг.
     */
    public void deleteMpa(Long id) {
        mpaStorage.deleteMpa(id);
    }
}