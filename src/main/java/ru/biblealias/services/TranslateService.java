package ru.biblealias.services;

import org.springframework.stereotype.Service;
import ru.biblealias.models.AnswersMessage;
import ru.biblealias.models.Command;
import ru.biblealias.models.Language;
import ru.biblealias.translators.EnglishTranslator;
import ru.biblealias.translators.RussianTranslator;
import ru.biblealias.translators.UkrainianTranslator;

@Service
public class TranslateService {
    private final RussianTranslator russianTranslator;
    private final EnglishTranslator englishTranslator;
    private final UkrainianTranslator ukrainianTranslator;

    public TranslateService(RussianTranslator russianTranslator, EnglishTranslator englishTranslator, UkrainianTranslator ukrainianTranslator) {
        this.russianTranslator = russianTranslator;
        this.englishTranslator = englishTranslator;
        this.ukrainianTranslator = ukrainianTranslator;
    }

    public Command translateToBot(String message, Language language) {
        return switch (message) {
            case "/start_new_game" -> Command.START_NEW_GAME;
            case "/stop_game" -> Command.STOP_GAME;
            case "/settings" -> Command.SETTINGS;
            case "/instruction" -> Command.INSTRUCTION;
            case "/send_feedback" -> Command.SEND_FEEDBACK;

            default -> switch (language) {
                case RUSSIAN -> russianTranslator.getBotsCommand(message);
                case UKRAINIAN -> ukrainianTranslator.getBotsCommand(message);
                case ENGLISH -> englishTranslator.getBotsCommand(message);
            };
        };

    }

    public String translateCommandToUser(Command command, Language language) {
        switch (language) {
            case RUSSIAN -> {
                return russianTranslator.translateCommandToUser(command);
            }
            case UKRAINIAN -> {
                return ukrainianTranslator.translateCommandToUser(command);
            }
            case ENGLISH -> {
                return englishTranslator.translateCommandToUser(command);
            }
            default -> {
                return "";
            }
        }
    }

    public String translateAnswersMessage(AnswersMessage message, Language language) {
        switch (language) {
            case RUSSIAN -> {
                return russianTranslator.translateAnswersMessage(message);
            }
            case UKRAINIAN -> {
                return ukrainianTranslator.translateAnswersMessage(message);
            }
            case ENGLISH -> {
                return englishTranslator.translateAnswersMessage(message);
            }
            default -> {
                return "";
            }
        }
    }

}
