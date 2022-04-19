package ru.biblealias.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.biblealias.BibleAliasBot;
import ru.biblealias.models.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AdminService {

    public static final String ADMIN_ID = "711842183";
    private final UsersService usersService;
    private final GameService gameService;
    private final WordsService wordsService;
    private final TasksService tasksService;
    private final ChequesService chequesService;
    private final ButtonsService buttonsService;
    private final TranslateService translateService;

    public AdminService(UsersService usersService, GameService gameService, WordsService wordsService, TasksService tasksService, ChequesService chequesService, ButtonsService buttonsService, TranslateService translateService) {
        this.usersService = usersService;
        this.gameService = gameService;
        this.wordsService = wordsService;
        this.tasksService = tasksService;
        this.chequesService = chequesService;
        this.buttonsService = buttonsService;
        this.translateService = translateService;
    }

    public List<SendMessage> getAnswer(Update update){
        List<SendMessage> messages = new ArrayList<>();
        String updateMessageText = update.getMessage().getText();

        if (updateMessageText.contains("пользовател")){
            messages = getUsers(updateMessageText);
        }
        else if (updateMessageText.contains("авторизуй")) {
            messages = setAuthorization(update, 0, null);
        }
        else if (updateMessageText.contains("отправь")){
            messages = sendSomeMessage(updateMessageText);
        }
        else if (updateMessageText.contains("кто сейчас играет")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(ADMIN_ID);
            StringBuilder stringBuilder = new StringBuilder("Сейчас играют ");
            Map<Long, Game> gameMap = gameService.getGamesMap();
            stringBuilder.append(gameMap.keySet().size()).append(" пользователей: ");
            for (Long userId: gameMap.keySet()) {
                stringBuilder.append("\n* ").append(gameMap.get(userId).getStartingTime().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")));
            }
            sendMessage.setText(stringBuilder.toString());
            messages.add(sendMessage);
        }
        else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(ADMIN_ID);
            sendMessage.setText("Команда не найдена");
            messages.add(sendMessage);
        }
        return messages;
    }

    public List<SendMessage> setAuthorization(Update update, long chatId, String cheque) {
        SendMessage messageToUser = new SendMessage();
        SendMessage messageToAdmin = new SendMessage();
        messageToAdmin.setChatId(ADMIN_ID);
        List<SendMessage> messages = new ArrayList<>();
        messages.add(messageToAdmin);
        messages.add(messageToUser);

        UsersMdl user;

        if (chatId != 0 && cheque != null) {
            user = usersService.setAuthorization(chatId, cheque);
        }
        else {
            String userName = update.getMessage().getText().split("@")[1].trim();
            user = usersService.setAuthorization(userName);
        }
        if (user.getChatId() != 0) {
            messageToUser.setChatId(user.getChatId() + "");
            messageToUser.setText(translateService.translateAnswersMessage(AnswersMessage.SUCCESSFUL_AUTHORIZATION, usersService.getUsersSettings(chatId).getLanguage()));
            String[] buttonsText = new String[1];
            buttonsText[0] = translateService.translateCommandToUser(Command.START_NEW_GAME, usersService.getUsersSettings(chatId).getLanguage());
            messageToUser.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
            messageToAdmin.setText("Пользователь " + user.getUserName() + " успешно авторизован!" + "\n" + user);
        }
        else {
            messageToAdmin.setText("Ошибка авторизации пользователя " + user);
        }

        return messages;
    }

    private List<SendMessage> getUsers(String updateMessageText) {
        List<SendMessage> messages = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ADMIN_ID);
        messages.add(sendMessage);

        StringBuilder answerTextBuilder = new StringBuilder();
        List<UsersMdl> allUsers = usersService.getAllUsers();
        List<UsersMdl> usersToShow;

        if (updateMessageText.contains("неавторизован")) {
            answerTextBuilder.append("Список неавторизованных пользователей: \n");
            usersToShow = allUsers.stream().filter(user -> !user.isAuthorized()).toList();
        }
        else if (updateMessageText.contains("авторизован")) {
            answerTextBuilder.append("Список авторизованных пользователей: \n");
            usersToShow = allUsers.stream().filter(user -> user.isAuthorized()).toList();
        }
        else {
            answerTextBuilder.append("Список всех пользователей: \n");
            usersToShow = allUsers;
        }

        AtomicInteger countOfUsers = new AtomicInteger();
        usersToShow.forEach(user -> answerTextBuilder.append("\n" + countOfUsers.incrementAndGet() + ". " + user.toString()));
        sendMessage.setText(answerTextBuilder.toString());

        return messages;
    }

    public List<SendMessage> sendSomeMessage(String updateMessageText) {
        List<SendMessage> messages = new ArrayList<>();
        if (updateMessageText.contains("всем")) {
            List<SendMessage> messageList = new ArrayList<>();
            String messageText = updateMessageText.split(":")[1].trim();
            Set<Long> usersChatIds;
            if (updateMessageText.contains("неавторизован")) {
                usersChatIds = this.usersService.getNotAuthorizedChatIdSet();
            }
            else if (updateMessageText.contains("авторизован")) {
                usersChatIds = this.usersService.getAuthorizedChatIdSet();
            }
            else {
                Set<Long> allUsersChatIds = new HashSet<>();
                this.usersService.getAllUsers().forEach(user -> allUsersChatIds.add(user.getChatId()));
                usersChatIds = allUsersChatIds;
            }
            usersChatIds.forEach(userChatId -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(userChatId.toString());
                sendMessage.setText(messageText);
                messageList.add(sendMessage);
            });

            SendMessage adminMessage = new SendMessage();
            adminMessage.setChatId(ADMIN_ID);
            adminMessage.setText("Сообщение отправлено");
            messageList.add(adminMessage);

            messages = messageList;
        }
        else if(updateMessageText.contains("@")) {
            String userName = updateMessageText.split("@")[1].split(" ")[0].trim();
            String messageText = updateMessageText.split(":")[1].trim();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(usersService.findUserByUserName(userName).getChatId() + "");
            sendMessage.setText(messageText);
            messages.add(sendMessage);

            SendMessage adminMessage = new SendMessage();
            adminMessage.setChatId(ADMIN_ID);
            adminMessage.setText("Сообщение отправлено");
            messages.add(adminMessage);
        }
        else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(ADMIN_ID);
            sendMessage.setText("Не понимаю, кому и что отправить");
            messages.add(sendMessage);
        }

        return messages;
    }


//    public void initTasks(){
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("C:/Users/artem/OneDrive/Рабочий стол/ЗаданияРождУкр.txt"));
//            while (reader.ready()){
//                TasksMdl task = new TasksMdl();
//                task.setTask(reader.readLine().trim());
//                task.setMode("OLD_TESTAMENT_UKR");
//                tasksService.saveTask(task);
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void initWords(){
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("C:/Users/artem/OneDrive/Рабочий стол/СловаРождУкр.txt"));
//            while (reader.ready()){
//                WordsMdl word = new WordsMdl();
//                word.setWord(reader.readLine().split("\\.")[1].trim());
//                word.setMode("Christmas_Ucr");
//                wordsService.saveWord(word);
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
