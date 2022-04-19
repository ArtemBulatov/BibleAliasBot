package ru.biblealias;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.biblealias.models.*;
import ru.biblealias.services.*;

import java.util.*;

@Component
public class BibleAliasBot extends TelegramLongPollingBot {

    private static final long FIRST_ADMIN_CHAT_ID = 711842183;
    private static final long SECOND_ADMIN_CHAT_ID = 1557569014;

    @Value("${bot.name}")
    private String botUserName;

    @Value("${bot.token}")
    private String botToken;

    private final TranslateService translateService;
    private final UsersService usersService;
    private final AdminService adminService;
    private final GameService gameService;
    private final SettingsService settingsService;
    private final ButtonsService buttonsService;
    private final FeedbackService feedbackService;
    private final ChequesService chequesService;
    private final Map<Long, Status> statusMap;
    private Set<Long> authorizedUsers;
    private Set<Long> usersWhoCanPlayTrialGame;
    private final Set<Long> inSettingsUsers;

    public BibleAliasBot(TranslateService translateService, UsersService usersService, AdminService adminService, GameService gameService, SettingsService settingsService, ButtonsService buttonsService, FeedbackService feedbackService, ChequesService chequesService) {
        this.translateService = translateService;
        this.usersService = usersService;
        this.adminService = adminService;
        this.gameService = gameService;
        this.settingsService = settingsService;
        this.buttonsService = buttonsService;
        this.feedbackService = feedbackService;
        this.chequesService = chequesService;
        this.authorizedUsers = this.usersService.getAuthorizedChatIdSet();
        this.usersWhoCanPlayTrialGame = this.usersService.getUsersWhoCanPlayTrialGame();
        this.inSettingsUsers = new HashSet<>();
        this.statusMap = new HashMap<>();
    }

    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        Language language = settingsService.getSettingsByChatId(chatId).getLanguage();
        Command command = translateService.translateToBot(messageText, language);

        List<SendMessage> answers = new ArrayList<>();

        if (isForAdmin(update)){
            if (messageText.contains("добавь чек")) {
                String newCheque = "#" + messageText.split("#")[1].trim();
                Cheque cheque = chequesService.addNewCheque(newCheque);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getMessage().getChatId() + "");
                sendMessage.setText("Номер заказа добавлен: " + cheque.getChequeNumber());
                answers.add(sendMessage);
            }
            else answers = adminService.getAnswer(update);
        }
        else if (command == Command.HELLO) {
            SendMessage helloMessage = new SendMessage();
            helloMessage.setChatId(chatId.toString());
            helloMessage.setText(
                    translateService.translateCommandToUser(Command.HELLO, language)
                    + ", " + update.getMessage().getChat().getFirstName() + "!"
            );
            answers.add(helloMessage);
        }
        else if (command == Command.SEND_FEEDBACK || command == Command.FEEDBACK_MESSAGE) {
            answers = feedbackService.getAnswer(update, command, language);
        }
        else if (authorizedUsers.contains(chatId) || usersWhoCanPlayTrialGame.contains(chatId)){
            if (usersWhoCanPlayTrialGame.contains(chatId) && command == Command.STOP_GAME) {
                usersWhoCanPlayTrialGame.remove(chatId);
            }

            if (command == Command.PLAY_USING_THE_CURRENT_SETTINGS) {
                inSettingsUsers.remove(chatId);
                answers.add(gameService.getAnswer(update, command, language));
            }
            else if (command == Command.SETTINGS || command == Command.CHANGE_SETTINGS) {
                //statusMap.put(chatId, Status.IN_SETTINGS);
                inSettingsUsers.add(chatId);
                answers = settingsService.getAnswer(update, command, language);
            }
            else if (inSettingsUsers.contains(chatId)) {
                if (command == Command.YES) {
                    inSettingsUsers.remove(chatId);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.NOW_YOU_CAN_PLAY, language));
                    String[] buttonsText = new String[1];
                    buttonsText[0] = translateService.translateCommandToUser(Command.START_NEW_GAME, language);
                    sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
                    answers.add(sendMessage);
                }
                else if (command == Command.NO) {
                    answers.add(settingsService.changeSomeSettings(chatId, language));
                }
                else {
                    answers = settingsService.getAnswer(update, command, language);
                }
            }
            else if (command == Command.INSTRUCTION) {
                //statusMap.put(chatId, Status.IN_SETTINGS);
                answers = settingsService.getInstruction(chatId.toString(), language);
            }
            else {
                answers.add(gameService.getAnswer(update, command, language));
//                    checkGameStatus(chatId);
            }
        }
        else if (messageText.equals("/start")) {
            UsersMdl user = usersService.createOrUpdateUser(update.getMessage().getChat());
            if (user != null) {
                answers.add(getNewUserMessage(user));
            }
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText("Выберите язык");
            String[] buttonsText = new String[2];
            buttonsText[0] = translateService.translateCommandToUser(Command.RUSSIAN, language);
            buttonsText[1] = translateService.translateCommandToUser(Command.UKRAINIAN, language);
            sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, true));
            answers.add(sendMessage);
        }
        else if (!authorizedUsers.contains(chatId) && (command == Command.RUSSIAN || command == Command.UKRAINIAN)){
            Settings settings = new Settings();
            if (command == Command.RUSSIAN) {
                settings.setLanguage(Language.RUSSIAN);
            }
            if (command == Command.UKRAINIAN) {
                settings.setLanguage(Language.UKRAINIAN);
            }
            settingsService.setSettingsByChatId(chatId, settings);
            answers.add(getWelcomeMessage(chatId.toString(), settings.getLanguage()));
        }
        else if (messageText.trim().matches("^#\\w{5}$")) {
            Cheque cheque = chequesService.findByChequeNumber(messageText.trim());
            if (cheque != null) {
                answers = adminService.setAuthorization(null, chatId, cheque.getChequeNumber());
                chequesService.deleteCheque(cheque);
                authorizedUsers = this.usersService.getAuthorizedChatIdSet();
            }
            else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId.toString());
                sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.INVALID_CODE, language));
                answers.add(sendMessage);
            }
        }
        else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.YOU_DO_NOT_HAVE_ACCESS_TO_THE_BOT, language));
            answers.add(sendMessage);
        }

        answers.forEach(sendMessage -> {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    // Геттеры, которые необходимы для наследования от TelegramLongPollingBot
    public String getBotUsername() {
        return botUserName;
    }

    public String getBotToken() {
        return botToken;
    }

    public void sendMessageToAdmin(SendMessage sendMessage) {
        sendMessage.setChatId(FIRST_ADMIN_CHAT_ID + "");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isForAdmin(Update update){
        return (update.getMessage().getChatId() == FIRST_ADMIN_CHAT_ID
                || update.getMessage().getChatId() == SECOND_ADMIN_CHAT_ID)
                &&  update.getMessage().getText().contains("Давид");
    }

    private SendMessage getWelcomeMessage(String chatId, Language language) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(translateService.translateAnswersMessage(AnswersMessage.WELCOME_MESSAGE, language));
        return message;
    }

    private SendMessage getNewUserMessage(UsersMdl user) {
        SendMessage message = new SendMessage();
        message.setChatId(FIRST_ADMIN_CHAT_ID + "");
        message.setText("Новый пользователь: " + user.toString());
        return message;
    }

    private void checkGameStatus(Long chatId) {
        if (gameService.isInGame(chatId.toString())){
            statusMap.put(chatId, Status.IN_GAME);
        }
        else statusMap.remove(chatId);
    }

}
