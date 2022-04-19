package ru.biblealias.translators;

import org.springframework.stereotype.Service;
import ru.biblealias.models.AnswersMessage;
import ru.biblealias.models.Command;

import java.util.Locale;

@Service
public class EnglishTranslator {

    public Command getBotsCommand(String message) {
        if(message.toLowerCase(Locale.ROOT).contains("hello")) {
            return Command.HELLO;
        }
        return Command.DONT_UNDERSTAND;
    }

    public String translateCommandToUser(Command command) {
        return switch (command) {
            case HELLO -> "Hi";
            case YES -> "Yes";
            case NO -> "No";
            case PLAY_USING_THE_CURRENT_SETTINGS -> "Играть, используя текущие настройки";
            case START_NEW_GAME -> "Начать новую игру";
            case STOP_GAME -> "Завершить игру";
            case SETTINGS -> "Настройки";
            case INSTRUCTION -> "Инструкция";
            case SEND_FEEDBACK -> "Оставить отзыв";
            case CHANGE_SETTINGS -> "Изменить настройки";
            case LANGUAGE -> "Язык";
            case RUSSIAN -> "Русский язык";
            case ENGLISH -> "English language";
            case UKRAINIAN -> "Українська мова";
            case GAME_MODE -> "Режим игры";
            case OLD_TESTAMENT -> "Ветхий Завет \uD83D\uDD4E";
            case NEW_TESTAMENT -> "Новый Завет ✝️";
            case ALL_BIBLE -> "Вся Библия \uD83D\uDCD6";
            case CHRISTMAS -> "Рождественский alias ✨";
            case ROUND_DURATION -> "Длительность раунда";
            case PENALTY -> "Штраф за неотгаданное слово";
            case PENALTY_ONE -> "1 очко";
            case PENALTY_TWO -> "2 очка";
            case PENALTY_ZERO -> "Не штрафовать";
            case GET_TASK -> "Получить задание \uD83D\uDCAC";
            case START_GUESSING_WORDS -> "Начать отгадывать слова";
            case GUESSED -> "Отгадали ✔️";
            case NOT_GUESSED -> "Не отгадали ❌";
            default -> "";
        };
    }

    public String translateAnswersMessage(AnswersMessage message) {
        return switch (message) {
            case INSTRUCTION -> """
                        С помощью этого бота можно играть в "Библейский alias", используя смартфон \uD83D\uDCF1 вместо карточек.
                        Для игры необходимо собрать минимум 4-х человек, поделить их на команды и играть по известным правилам игры в alias.
                        В разделе "Настройки" можно выбрать режим игры (Ветхий Завет, Новый Завет, вся Библия или Рождественский alias), длительность раунда и штраф за неотгаданное слово.
                        Текущие настройки напоминаются в начале каждой новой игры. Также в начале игры нужно ввести названия команд, с помошью которых бот будет контролировать очередность хода.
                        Отсчет времени раунда начинается с того момента, когда было получено первое слово для отгадывания.
                        Желаем хорошего времяпровождения! \uD83D\uDE07
                        """;
            case ENTER_TEAM_NAMES -> "Введите названия команд через запятую. Сколько названий введёте, столько и будет команд в этой игре.";
            case SELECT_GAME_MODE -> "Выберите режим игры";
            case SELECT_ROUND_DURATION -> "Выберите или введите свою длительность раунда ⏳ (в секундах)";
            case HOW_MUCH_POINTS_OF_PENALTY -> "На сколько очков штрафовать команду за неотгаданное слово?";
            case ROUND_DURATION_NOT_LESS -> "Длина раунда не может быть меньше 15 секунд!";
            case ROUND_DURATION_NOT_MORE -> "Длина раунда не может быть больше 3 минут!";
            case WANT_TO_EXIT_THE_SETTINGS -> "Хотите выйти из настроек?";
            case WHAT_TO_CHANGE -> "Что хотите изменить?";
            case CHOOSE_LANGUAGE -> "Выберите язык";
            case NOW_YOU_CAN_PLAY -> "Теперь можно собрать друзей и поиграть \uD83D\uDE07";
            case INVALID_CODE -> "Неверный код авторизации";
            case SUCCESSFUL_AUTHORIZATION -> "";
            case YOU_DO_NOT_HAVE_ACCESS_TO_THE_BOT -> "К сожалению, у Вас нет доступа к боту \uD83D\uDE15 " +
                    "Чтобы играть в \"Библейский alias\" купите доступ, пройдя по ссылке в наш магазин: " +
                    "https://biblealias.ru/products/Bibleysky-alias-Bot-p406365873" +
                    "\nЕсли у Вас уже есть код авторизации (номер Вашего заказа в формате #*****), отправьте его в сообщении.";
            case WELCOME_MESSAGE -> """
                Привет! \uD83D\uDC4B\s
                Добро пожаловать в чат-бот игры "Библейский alias"!
                С помощью этого бота можно играть в нашу игру, используя смартфон \uD83D\uDCF1 вместо карточек.
                Для получения доступа к боту пройдите по ссылке в наш магазин и оформите подписку:
                https://biblealias.ru/products/Bibleysky-alias-Bot-p406365873
                Если у Вас уже есть код авторизации (номер Вашего заказа в формате #*****), отправьте его в сообщении.
                """;
            case CANNOT_STOP_GAME_WITHOUT_STARTING -> "Вы не можете завершить игру даже не начав \uD83D\uDE09";
            case WINNER_TEAM -> "Победила команда ";
            case WINNER_TEAMS -> "Победили команды: ";
            case DRAW -> "Ничья! \uD83D\uDC4D";
            case NUMBER_OF_POINTS -> "Количество очков";
            case HELLO_TEAMS -> "Привет \uD83D\uDC4B командам ";
            case YOUR_ROUND_DURATION_IS -> "Длительность вашего раунда составляет";
            case IT_IS_TIME_FOR_EVERY_TEAM -> " секунд — это время, которое поочерёдно даётся каждой команде на объяснение и отгадывание слов. "
                    + "По истечении времени ход переходит к другой команде.";
            case SO_FIRST_TEAM_IS -> "Итак, первой начинает команда ";
            case GIVE_THEM_PHONE -> "Передайте им телефон \uD83D\uDCF2";
            case ARE_YOU_READY_TO_START -> ", вы готовы начать? Если да, то нажмите \"Получить задание\". "
                    + "Прочтите его вслух, а затем начните отгадывать слова \uD83D\uDE09";
            case TURNS_TEAM_IS -> "Ход команды";
            case TASK_IS -> "";
            case WORD_IS -> "Слово";

            case TIME_OF_TEAM -> "Время команды ";
            case IS_OVER -> " вышло! ⏳";
            case TURN_GOES_TO_TEAM -> "Ход переходит команде: ";

            case SORRY_I_DONT_UNDERSTAND -> "Извините, я Вас не понимаю";
            case TRY_NEW_GAME -> "Попробуйте начать новую игру \uD83E\uDD17";

            case THANKS_FOR_FEEDBACK -> "Благодарим за обратную связь \uD83D\uDE0A";
            case FEEDBACK_INSTRUCTION -> "Мы будем очень рады получить отзыв или пожелания от Вас \uD83D\uDCAC " +
                    "Для этого отправьте сообщение в виде: \"Отзыв: ...\" и мы его обязательно прочтём \uD83E\uDD17";
        };
    }
}
