package ru.biblealias.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.biblealias.models.AnswersMessage;
import ru.biblealias.models.Command;
import ru.biblealias.models.Language;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {
    private final UsersService usersService;
    private final TranslateService translateService;

    public FeedbackService(UsersService usersService, TranslateService translateService) {
        this.usersService = usersService;
        this.translateService = translateService;
    }

    public List<SendMessage> getAnswer(Update update, Command command, Language language) {
        List<SendMessage> answerList = new ArrayList<>();
        String messageText = update.getMessage().getText().trim();
        if (command == Command.SEND_FEEDBACK) {
            answerList.add(getFeedbackInstruction(update.getMessage().getChatId().toString(), language));
        }
        else if (command == Command.FEEDBACK_MESSAGE) {
            answerList.add(getFeedbackMessageToAdmin(update));
            answerList.add(getAnswerToFeedback(update.getMessage().getChatId().toString(), language));
        }
        return answerList;
    }

    public SendMessage getFeedbackMessageToAdmin(Update update) {
        SendMessage feedbackMessage = new SendMessage();
        feedbackMessage.setChatId(AdminService.ADMIN_ID);
        feedbackMessage.setText("Отзыв от пользователя:" + usersService.findUserByChatId(update.getMessage().getChatId())
                + "\n" + update.getMessage().getText()
        );
        return feedbackMessage;
    }

    public SendMessage getAnswerToFeedback(String chatId, Language language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(translateService.translateAnswersMessage(AnswersMessage.THANKS_FOR_FEEDBACK, language));
        return sendMessage;
    }

    public SendMessage getFeedbackInstruction(String chatId, Language language) {
        SendMessage feedbackInstruction = new SendMessage();
        feedbackInstruction.setChatId(chatId);
        feedbackInstruction.setText(translateService.translateAnswersMessage(AnswersMessage.FEEDBACK_INSTRUCTION, language));
        return feedbackInstruction;
    }
}
