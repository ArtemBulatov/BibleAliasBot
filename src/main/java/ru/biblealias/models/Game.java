package ru.biblealias.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.Data;

@Data
public class Game implements Runnable{

    LocalDateTime startingTime;
    String chatId;
    boolean isGameAlive;
    boolean timeoutIsOver;
    Settings settings;
    List<TeamsMdl> teams;
    TeamsMdl firstTeam;
    TeamsMdl secondTeam;
    List<WordsMdl> words;
    List<TasksMdl> tasks;

    public Game (Settings settings) {
        this.startingTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        this.settings = settings;
    }

    @Override
    public void run() {
        while (isGameAlive){
            if (!timeoutIsOver){
                try {
                    Thread.sleep(1000L * settings.getTimeoutSeconds());
                    timeoutIsOver = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public TeamsMdl getTurnsTeam() {
        return findTurnsTeam(teams);
    }

    public TeamsMdl getNextTurnsTeam() {
        TeamsMdl turnsTeam = findTurnsTeam(teams);
        int turnsTeamIndex = teams.indexOf(turnsTeam);
        if (turnsTeamIndex + 1 < teams.size()) {
            turnsTeam = teams.get(turnsTeamIndex + 1);
        }
        else {
            turnsTeam = teams.get(0);
        }
        return turnsTeam;
    }

    public String getWord() {
        String word = "Слова закончились \uD83D\uDE2F";

        if (!words.isEmpty()){
            int index = (int) (Math.random() * words.size()-1) + 1;
            word = words.get(index).getWord();
            words.remove(index);
        }

        return word;
    }

    public String getTask() {
        String task = "Задания закончились \uD83D\uDE2F";

        if (!tasks.isEmpty()){
            int index = (int) (Math.random() * tasks.size()-1) + 1;
            task = tasks.get(index).getTask();
            tasks.remove(index);
        }

        return task;
    }

    public void changeTeamTurns() {
        TeamsMdl turnsTeam = findTurnsTeam(teams);
        int turnsTeamIndex = teams.indexOf(turnsTeam);
        if (turnsTeamIndex + 1 < teams.size()) {
            teams.get(turnsTeamIndex + 1).setTurn(true);
        }
        else {
            teams.get(0).setTurn(true);
        }
        turnsTeam.setTurn(false);
    }

    public List<TeamsMdl> getWinnerTeams () {
        TeamsMdl winnerTeam = new TeamsMdl();
        for (int i = 0; i < teams.size(); i ++) {
            if (teams.get(i).getPoints() > winnerTeam.getPoints()) {
                winnerTeam = teams.get(i);
            }
        }
        int winnerPoints = winnerTeam.getPoints();
        List<TeamsMdl> winnerTeams = teams.stream().filter(team -> team.getPoints() == winnerPoints).toList();

        return winnerTeams;
    }

    private TeamsMdl findTurnsTeam(List<TeamsMdl> teamsList) {
        return teamsList.stream().filter(team -> team.isTurn).findAny().orElse(teamsList.get(0));
    }
}
