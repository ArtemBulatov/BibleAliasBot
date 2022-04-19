package ru.biblealias.services;

import org.springframework.stereotype.Service;
import ru.biblealias.models.Language;
import ru.biblealias.models.WordsMdl;
import ru.biblealias.repositories.WordsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class WordsService {
    private final WordsRepository wordsRepository;

    public WordsService(WordsRepository wordsRepository) {
        this.wordsRepository = wordsRepository;
    }

    public WordsMdl saveWord(WordsMdl word) {
        return wordsRepository.save(word);
    }

    public List<WordsMdl> getAllWords() {
        return wordsRepository.findAll();
    }

    public List<WordsMdl> getOldTestamentWords() {
        return wordsRepository.getAllByMode("old");
    }

    public List<WordsMdl> getNewTestamentWords() {
        return wordsRepository.getAllByMode("new");
    }

    public List<WordsMdl> getAllBibleWords() {
        List<WordsMdl> allBibleWords = new ArrayList<>();
        allBibleWords.addAll(wordsRepository.getAllByMode("old"));
        allBibleWords.addAll(wordsRepository.getAllByMode("new"));
        return allBibleWords;
    }

    public List<WordsMdl> getChristmasWords(Language language) {
        return switch (language) {
            case RUSSIAN -> wordsRepository.getAllByMode("Christmas");
            case UKRAINIAN -> wordsRepository.getAllByMode("Christmas_Ucr");
            case ENGLISH -> wordsRepository.getAllByMode("Christmas");
        };
    }
}
