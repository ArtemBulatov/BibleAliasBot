package ru.biblealias.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class ButtonsService {

    public ReplyKeyboardMarkup getReplyButtons(String[] buttonsText, boolean isOneTime) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(isOneTime);
        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();

        int count = 0;
        for (String buttonText : buttonsText){
            count++;
            if (count > 2) {
                KeyboardRow keyboardNextRow = new KeyboardRow();
                keyboardNextRow.add(new KeyboardButton(buttonText));
                keyboard.add(keyboardNextRow);
            }
            else {
                keyboardFirstRow.add(new KeyboardButton(buttonText));
            }
        }
        keyboard.add(0, keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineButton(String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonsList = new ArrayList<>();
        List<InlineKeyboardButton> buttons= new ArrayList<>();
        buttons.add(new InlineKeyboardButton(text));
        buttonsList.add(buttons);
        inlineKeyboardMarkup.setKeyboard(buttonsList);
        return inlineKeyboardMarkup;
    }
}
