package ru.biblealias.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.biblealias.models.WordsMdl;
import java.util.List;

public interface WordsRepository extends JpaRepository<WordsMdl, Integer> {
    List<WordsMdl> getAllByMode(String mode);
}
