package ru.biblealias.models;

import lombok.Data;

@Data
public class TeamsMdl {

    boolean isTurn;
    private String teamName;
    private int points;
}
