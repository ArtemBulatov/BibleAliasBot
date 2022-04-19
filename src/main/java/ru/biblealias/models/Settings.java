package ru.biblealias.models;

import lombok.Data;

@Data
public class Settings {
    private int timeoutSeconds = 60;
    private int penalty = 0;
    private GameMode gameMode = GameMode.ALL_BIBLE;
    private Language language = Language.RUSSIAN;
}
