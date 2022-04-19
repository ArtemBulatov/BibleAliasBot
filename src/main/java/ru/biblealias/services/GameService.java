package ru.biblealias.services;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.biblealias.models.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@EnableScheduling
public class GameService {

    private final UsersService usersService;
    private final WordsService wordsService;
    private final TasksService tasksService;
    private final ButtonsService buttonsService;
    private final SettingsService settingsService;
    private final TranslateService translateService;
    private final Map<Long, Game> gamesMap;

    public GameService(UsersService usersService, WordsService wordsService, TasksService tasksService, ButtonsService buttonsService, SettingsService settingsService, TranslateService translateService) {
        this.usersService = usersService;
        this.wordsService = wordsService;
        this.tasksService = tasksService;
        this.buttonsService = buttonsService;
        this.settingsService = settingsService;
        this.translateService = translateService;
        gamesMap = new HashMap<>();
    }

    public SendMessage getAnswer(Update update, Command command, Language language){
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        String receivedMessage = update.getMessage().getText();

        if (command == Command.START_NEW_GAME){
            usersService.createOrUpdateUser(update.getMessage().getChat());
            Game game = new Game(new Settings());
            gamesMap.put(chatId, game);
            return settingsService.getUsualSettings(update.getMessage().getChatId(), language);
        }
        else if (command == Command.PLAY_USING_THE_CURRENT_SETTINGS) {
            if (gamesMap.containsKey(chatId)) {
                gamesMap.get(chatId).setSettings(usersService.getUsersSettings(update.getMessage().getChatId()));
            }
            else {
                Game game = new Game(usersService.getUsersSettings(update.getMessage().getChatId()));
                gamesMap.put(chatId, game);
            }
            sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.ENTER_TEAM_NAMES, language));
        }
        else if (gamesMap.containsKey(chatId)){
            switch (command){
                case GET_TASK -> {
                    String tasksText = gamesMap.get(chatId).getTask();
                    sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.TASK_IS, language) + ": \n" + tasksText);
                    String[] buttonsText = new String[1];
                    buttonsText[0] = translateService.translateCommandToUser(Command.START_GUESSING_WORDS, language);
                    sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
                    return sendMessage;
                }
                case START_GUESSING_WORDS -> {
                    sendMessage = getNewWordSendMessage(chatId, language);
                    Game game = gamesMap.get(chatId);
                    game.setTimeoutIsOver(false);
                    Thread gameThread = new Thread(game);
                    gameThread.start();
                    return sendMessage;
                }
                case GUESSED -> {
                    Game thisGame = gamesMap.get(chatId);
                    TeamsMdl turnsTeam = thisGame.getTurnsTeam();
                    int points = turnsTeam.getPoints();
                    turnsTeam.setPoints(++points);

                    SendMessage sendTimeoutMessage = checkTimeout(gamesMap.get(chatId), chatId, language);
                    if (sendTimeoutMessage.getChatId() != null) {
                        sendMessage = sendTimeoutMessage;
                    }
                    else {
                        sendMessage = getNewWordSendMessage(chatId, language);
                    }
                    return sendMessage;
                }
                case NOT_GUESSED -> {
                    Game thisGame = gamesMap.get(chatId);
                    TeamsMdl turnsTeam = thisGame.getTurnsTeam();
                    int points = turnsTeam.getPoints();
                    turnsTeam.setPoints(points - thisGame.getSettings().getPenalty());

                    SendMessage sendTimeoutMessage = checkTimeout(gamesMap.get(chatId), chatId, language);
                    if (sendTimeoutMessage.getChatId() != null) {
                        sendMessage = sendTimeoutMessage;
                    }
                    else {
                        sendMessage = getNewWordSendMessage(chatId, language);
                    }
                    return sendMessage;
                }
                case STOP_GAME -> {
                    if (gamesMap.get(chatId).getTeams() == null || gamesMap.get(chatId).getTeams().isEmpty()){
                        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.CANNOT_STOP_GAME_WITHOUT_STARTING, language));
                        String[] buttonsText = new String[1];
                        buttonsText[0] = translateService.translateCommandToUser(Command.START_NEW_GAME, language);
                        sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
                        return sendMessage;
                    }
                    List<TeamsMdl> winnerTeams = gamesMap.get(chatId).getWinnerTeams();
                    List<TeamsMdl> allTeams = gamesMap.get(chatId).getTeams();
                    String winnerText;

                    if (winnerTeams.size() == 1) {
                        winnerText = translateService.translateAnswersMessage(AnswersMessage.WINNER_TEAM, language)
                                + winnerTeams.get(0).getTeamName() + "! \uD83E\uDD73";
                    }
                    else if (winnerTeams.size() == allTeams.size()) {
                        winnerText = translateService.translateAnswersMessage(AnswersMessage.DRAW, language);
                    }
                    else {
                        winnerText = translateService.translateAnswersMessage(AnswersMessage.WINNER_TEAMS, language)
                                + getTeamsNames(winnerTeams) + "! \uD83E\uDD73";
                    }

                    StringBuilder teamsPoints = new StringBuilder();
                    teamsPoints.append("\n")
                            .append(translateService.translateAnswersMessage(AnswersMessage.NUMBER_OF_POINTS, language))
                            .append(": ");
                    allTeams.forEach(
                            team -> teamsPoints
                                    .append("\n")
                                    .append(team.getTeamName())
                                    .append(":  ")
                                    .append(getIconForNumber(team.getPoints())));

                    sendMessage.setText(winnerText + teamsPoints);

                    gamesMap.get(chatId).setGameAlive(false);
                    gamesMap.remove(chatId);

                    String[] buttonsText = new String[1];
                    buttonsText[0] = translateService.translateCommandToUser(Command.START_NEW_GAME, language);
                    sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
                    addGameCount(update.getMessage().getChatId());
                    return sendMessage;
                }
                default -> {
                    if (receivedMessage.matches(".*,.*")) {
                        String[] teamsNames = receivedMessage.split(",");

                        List<TeamsMdl> teamsList = new ArrayList<>();
                        for (String teamsName : teamsNames) {
                            TeamsMdl team = new TeamsMdl();
                            team.setTeamName("\"" + teamsName.trim() + "\"");
                            teamsList.add(team);
                        }

                        Game game = gamesMap.get(chatId);
                        game.setTeams(teamsList);
                        game.getTeams().get(0).setTurn(true);

                        switch (game.getSettings().getGameMode()) {
                            case OLD_TESTAMENT -> {
                                game.setWords(wordsService.getOldTestamentWords());
                                game.setTasks(tasksService.getOldTestamentTasks());
                            }
                            case NEW_TESTAMENT -> {
                                game.setWords(wordsService.getNewTestamentWords());
                                game.setTasks(tasksService.getNewTestamentTasks());
                            }
                            case ALL_BIBLE -> {
                                game.setWords(wordsService.getAllBibleWords());
                                game.setTasks(tasksService.getAllBibleTasks());
                            }
                            case CHRISTMAS -> {
                                game.setWords(wordsService.getChristmasWords(language));
                                game.setTasks(tasksService.getChristmasTasks(language));
                            }
                        }
                        game.setGameAlive(true);

                        String turnsTeamName = game.getTurnsTeam().getTeamName();

                        List<TeamsMdl> gameTeams = game.getTeams();
                        String penalty = translateService.translateCommandToUser(Command.PENALTY_ZERO, language);
                        if (game.getSettings().getPenalty() == 1) {
                            penalty = translateService.translateCommandToUser(Command.PENALTY_ONE, language);
                        }
                        if (game.getSettings().getPenalty() == 2) {
                            penalty = translateService.translateCommandToUser(Command.PENALTY_TWO, language);
                        }

                        sendMessage.setChatId(String.valueOf(chatId));
                        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.HELLO_TEAMS, language)
                                + getTeamsNames(gameTeams) + "!"
                                + "\n\n"
                                + translateService.translateAnswersMessage(AnswersMessage.YOUR_ROUND_DURATION_IS, language) + " "
                                + game.getSettings().getTimeoutSeconds()
                                + translateService.translateAnswersMessage(AnswersMessage.IT_IS_TIME_FOR_EVERY_TEAM, language)
                                + "\n" + translateService.translateCommandToUser(Command.GAME_MODE, language) + ": "
                                + translateService.translateCommandToUser(Command.valueOf(game.getSettings().getGameMode().toString()), language) + "."
                                + "\n" + translateService.translateCommandToUser(Command.PENALTY, language) + ": " + penalty + "."
                                + "\n\n"
                                + translateService.translateAnswersMessage(AnswersMessage.SO_FIRST_TEAM_IS, language)
                                + turnsTeamName + "! "
                                + translateService.translateAnswersMessage(AnswersMessage.GIVE_THEM_PHONE, language)
                                + "\n\n" + turnsTeamName
                                + translateService.translateAnswersMessage(AnswersMessage.ARE_YOU_READY_TO_START, language)
                        );

                        String[] buttonsText = new String[1];
                        buttonsText[0] = translateService.translateCommandToUser(Command.GET_TASK, language);
                        sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
                    }
                    else {
                        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.SORRY_I_DONT_UNDERSTAND, language)
                                + " \uD83D\uDE14");
                    }
                    return sendMessage;
                }
            }
        }
        else if (command == Command.STOP_GAME){
            sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.CANNOT_STOP_GAME_WITHOUT_STARTING, language));
            String[] buttonsText = new String[1];
            buttonsText[0] = translateService.translateCommandToUser(Command.START_NEW_GAME, language);
            sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
            return sendMessage;
        }
        else {
            sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.SORRY_I_DONT_UNDERSTAND, language)
                    + ". " + translateService.translateAnswersMessage(AnswersMessage.TRY_NEW_GAME, language));
            String[] buttonsText = new String[1];
            buttonsText[0] = translateService.translateCommandToUser(Command.START_NEW_GAME, language);
            sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
        }
        return sendMessage;
    }

    private SendMessage getNewWordSendMessage(long chatId, Language language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.enableMarkdownV2(true);
        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.TURNS_TEAM_IS, language)
                + ": " + gamesMap.get(chatId).getTurnsTeam().getTeamName()
                + "\n" + translateService.translateAnswersMessage(AnswersMessage.WORD_IS, language)
                + ": \n*" + gamesMap.get(chatId).getWord() + "*");

        String[] buttonsText = new String[2];
        buttonsText[0] = translateService.translateCommandToUser(Command.GUESSED, language);
        buttonsText[1] = translateService.translateCommandToUser(Command.NOT_GUESSED, language);
        sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));

        return sendMessage;
    }

    private void addGameCount(long chatId) {
        UsersMdl user = usersService.findUserByChatId(chatId);
        user.setGamesCount(user.getGamesCount() + 1);
        usersService.updateUserByChatId(chatId, user);
    }

    private SendMessage checkTimeout(Game game, long chatId, Language language){
        SendMessage sendMessage = new SendMessage();

        if (game.isTimeoutIsOver()){
            String turnsTeamName = gamesMap.get(chatId).getTurnsTeam().getTeamName();
            String notTurnsTeamName = gamesMap.get(chatId).getNextTurnsTeam().getTeamName();

            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.TIME_OF_TEAM, language)
                    + turnsTeamName + translateService.translateAnswersMessage(AnswersMessage.IS_OVER, language)
                    + "\n" + translateService.translateAnswersMessage(AnswersMessage.TURN_GOES_TO_TEAM, language)
                    + notTurnsTeamName + "! "
                    + translateService.translateAnswersMessage(AnswersMessage.GIVE_THEM_PHONE, language)
                    + "\n" + notTurnsTeamName
                    + translateService.translateAnswersMessage(AnswersMessage.ARE_YOU_READY_TO_START, language)
            );

            String[] buttonsText = new String[1];
            buttonsText[0] = translateService.translateCommandToUser(Command.GET_TASK, language);
            sendMessage.setReplyMarkup(buttonsService.getReplyButtons(buttonsText, false));
            game.changeTeamTurns();
        }
        return sendMessage;
    }

    private String getIconForNumber(int number){
        if (number == 10){
            return "🔟";
        }
        if (number == -10){
            return "➖🔟";
        }
        StringBuilder result = new StringBuilder();
        String[] partsOfNumber = String.valueOf(number).split("");
        for (String part: partsOfNumber) {
            switch (part){
                case "0"-> result.append("0️⃣");
                case "1"-> result.append("1️⃣");
                case "2"-> result.append("2️⃣");
                case "3"-> result.append("3️⃣");
                case "4"-> result.append("4️⃣");
                case "5"-> result.append("5️⃣");
                case "6"-> result.append("6️⃣");
                case "7"-> result.append("7️⃣");
                case "8"-> result.append("8️⃣");
                case "9"-> result.append("9️⃣");
            }
        }
        if (number < 0) {
            return "➖" + result;
        }
        return result.toString();
    }

    private String getTeamsNames(List<TeamsMdl> teams) {
        StringBuilder teamsNamesBuilder = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            if (i == teams.size() - 2) {
                teamsNamesBuilder.append(teams.get(i).getTeamName()).append(" и ").append(teams.get(i + 1).getTeamName());
                break;
            }
            teamsNamesBuilder.append(teams.get(i).getTeamName()).append(", ");
        }
        return teamsNamesBuilder.toString();
    }

    public boolean isInGame(String chatId) {
        return gamesMap.containsKey(chatId);
    }

    public Map<Long, Game> getGamesMap() {
        return gamesMap;
    }

    @Scheduled(fixedDelay = 10800000)
    private void checkAndRemoveOldGames() {
        synchronized (gamesMap) {
            LocalDateTime nowDateTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
            for (Long userId: gamesMap.keySet()) {
                if (gamesMap.get(userId).getStartingTime().plusHours(12).isBefore(nowDateTime)) {
                    gamesMap.remove(userId);
                }
            }
        }

    }

}
