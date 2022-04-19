package ru.biblealias.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.biblealias.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettingsService {
    private final UsersService usersService;
    private final ButtonsService buttonsService;
    private final TranslateService translateService;
    private final Map<Long, Settings> usersSettings;

    public SettingsService(UsersService usersService, ButtonsService buttonsService, TranslateService translateService) {
        this.usersService = usersService;
        this.buttonsService = buttonsService;
        this.translateService = translateService;
        this.usersSettings = new HashMap<>();
    }

    public List<SendMessage> getAnswer(Update update, Command command, Language language){
        long chatId = update.getMessage().getChatId();
        String receivedMessage = update.getMessage().getText();
        List<SendMessage> messages = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());

        switch (command) {
            case PLAY_USING_THE_CURRENT_SETTINGS -> sendMessage
                    .setText(translateService.translateAnswersMessage(AnswersMessage.ENTER_TEAM_NAMES, language));
            case CHANGE_SETTINGS -> sendMessage = changeSomeSettings(update.getMessage().getChatId(), language);
            case LANGUAGE -> {
                sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.CHOOSE_LANGUAGE, language));
                String[] buttonsText = new String[2];
                buttonsText[0] = translateService.translateCommandToUser(Command.RUSSIAN, language);
                buttonsText[1] = translateService.translateCommandToUser(Command.UKRAINIAN, language);
//                buttonsText[2] = translateService.translateCommandToUser(Command.ENGLISH, language);
                sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, true));
            }
            case RUSSIAN, UKRAINIAN, ENGLISH -> {
                Settings settings = usersSettings.get(chatId);
                switch (command) {
                    case RUSSIAN -> settings.setLanguage(Language.RUSSIAN);
                    case UKRAINIAN -> settings.setLanguage(Language.UKRAINIAN);
                    case ENGLISH -> settings.setLanguage(Language.ENGLISH);
                }
                usersService.setSettingsToUser(chatId, settings);
                sendMessage = getUsualSettings(chatId, settings.getLanguage());
            }
            case GAME_MODE -> {
                sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.SELECT_GAME_MODE, language));
                String[] buttonsText = new String[4];
                buttonsText[0] = translateService.translateCommandToUser(Command.OLD_TESTAMENT, language);
                buttonsText[1] = translateService.translateCommandToUser(Command.NEW_TESTAMENT, language);
                buttonsText[2] = translateService.translateCommandToUser(Command.ALL_BIBLE, language);
                buttonsText[3] = translateService.translateCommandToUser(Command.CHRISTMAS, language);
                sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, true));
            }
            case OLD_TESTAMENT, NEW_TESTAMENT, ALL_BIBLE, CHRISTMAS -> {
                Settings settings = usersSettings.get(chatId);
                switch (command) {
                    case OLD_TESTAMENT -> settings.setGameMode(GameMode.OLD_TESTAMENT);
                    case NEW_TESTAMENT -> settings.setGameMode(GameMode.NEW_TESTAMENT);
                    case ALL_BIBLE -> settings.setGameMode(GameMode.ALL_BIBLE);
                    case CHRISTMAS -> settings.setGameMode(GameMode.CHRISTMAS);
                }
                usersService.setSettingsToUser(chatId, settings);
                sendMessage = getUsualSettings(chatId, language);
            }
            case ROUND_DURATION -> {
                sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.SELECT_ROUND_DURATION, language));
                String[] buttonsText = new String[3];
                buttonsText[0] = "30";
                buttonsText[1] = "45";
                buttonsText[2] = "60";
                sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
            }
            case PENALTY -> {
                sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.HOW_MUCH_POINTS_OF_PENALTY, language));
                String[] buttonsText = new String[3];
                buttonsText[0] = translateService.translateCommandToUser(Command.PENALTY_ONE, language);
                buttonsText[1] = translateService.translateCommandToUser(Command.PENALTY_TWO, language);
                buttonsText[2] = translateService.translateCommandToUser(Command.PENALTY_ZERO, language);
                sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, true));
            }
            case PENALTY_ONE, PENALTY_TWO, PENALTY_ZERO -> {
                Settings settings = usersSettings.get(chatId);
                switch (command) {
                    case PENALTY_ONE -> settings.setPenalty(1);
                    case PENALTY_TWO -> settings.setPenalty(2);
                    case PENALTY_ZERO -> settings.setPenalty(0);
                }
                usersService.setSettingsToUser(chatId, settings);
                sendMessage = getUsualSettings(chatId, language);
            }
            default -> {
                if (command == Command.SETTINGS) {
                    sendMessage = getUsualSettings(update.getMessage().getChatId(), language);
                }
                else if (receivedMessage.trim().matches("^\\d+$")){
                    int number = Integer.parseInt(receivedMessage.trim());
                    if (number < 15){
                        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.ROUND_DURATION_NOT_LESS, language));
                    }
                    else if (number > 180) {
                        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.ROUND_DURATION_NOT_MORE, language));
                    }
                    else {
                        Settings settings = usersSettings.get(chatId);
                        settings.setTimeoutSeconds(number);
                        usersService.setSettingsToUser(chatId, settings);
                        sendMessage = getUsualSettings(chatId, language);
                    }
                }
                else {
                    sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.WANT_TO_EXIT_THE_SETTINGS, language));
                    String[] buttonsText = new String[2];
                    buttonsText[0] = translateService.translateCommandToUser(Command.YES, language);
                    buttonsText[1] = translateService.translateCommandToUser(Command.NO, language);
                    sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, true));
                }
            }
        }
        messages.add(sendMessage);
        return messages;
    }

    public SendMessage changeSomeSettings(long chatId, Language language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId + "");
        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.WHAT_TO_CHANGE, language));
        String[] buttonsText = new String[4];
        buttonsText[0] = translateService.translateCommandToUser(Command.LANGUAGE, language);
        buttonsText[1] = translateService.translateCommandToUser(Command.GAME_MODE, language);
        buttonsText[2] = translateService.translateCommandToUser(Command.ROUND_DURATION, language);
        buttonsText[3] = translateService.translateCommandToUser(Command.PENALTY, language);
        sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, true));
        return sendMessage;
    }

    public SendMessage getUsualSettings(long chatId, Language language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId + "");

        Settings settings = getSettingsByChatId(chatId);
        String penalty = translateService.translateCommandToUser(Command.PENALTY_ZERO, language);;
        if (settings.getPenalty() == 1) {
            penalty = translateService.translateCommandToUser(Command.PENALTY_ONE, language);
        }
        if (settings.getPenalty() == 2) {
            penalty = translateService.translateCommandToUser(Command.PENALTY_TWO, language);;
        }
        sendMessage.enableMarkdownV2(true);
        sendMessage.setText(
                "⚙ " + translateService.translateCommandToUser(Command.SETTINGS, language) +" ⚙"
                        + "\n\n" + translateService.translateCommandToUser(Command.LANGUAGE, language)  + ": "
                        + "*" + translateService.translateCommandToUser(Command.valueOf(settings.getLanguage().toString()), language)+ "*"
                        + "\n" + translateService.translateCommandToUser(Command.GAME_MODE, language) + ": "
                        + "*" + translateService.translateCommandToUser(Command.valueOf(settings.getGameMode().toString()), language) + "*"
                        + "\n" + translateService.translateCommandToUser(Command.ROUND_DURATION, language) + ": "
                        + "*" + settings.getTimeoutSeconds() + " сек" + "*"
                        + "\n" + translateService.translateCommandToUser(Command.PENALTY, language) + ": " + "*" + penalty + "*"
        );
        String[] buttonsText = new String[2];
        buttonsText[0] = translateService.translateCommandToUser(Command.PLAY_USING_THE_CURRENT_SETTINGS, language);
        buttonsText[1] = translateService.translateCommandToUser(Command.CHANGE_SETTINGS, language);
        sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, true));
        return sendMessage;
    }

    public List<SendMessage> getInstruction(String chatId, Language language) {
        List<SendMessage> messages = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.INSTRUCTION, language));
        messages.add(sendMessage);
        return messages;
    }

    public Settings getSettingsByChatId(long chatId) {
        Settings settings;
        if (usersSettings.containsKey(chatId)) {
            settings = usersSettings.get(chatId);
        }
        else {
            settings = usersService.getUsersSettings(chatId);
            usersSettings.put(chatId, settings);
        }
        return settings;
    }

    public void setSettingsByChatId(long chatId, Settings settings) {
        usersService.setSettingsToUser(chatId, settings);
        usersSettings.put(chatId, settings);
    }
}
