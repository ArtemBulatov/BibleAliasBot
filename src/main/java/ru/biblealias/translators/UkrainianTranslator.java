package ru.biblealias.translators;

import org.springframework.stereotype.Service;
import ru.biblealias.models.AnswersMessage;
import ru.biblealias.models.Command;

import java.util.Locale;

@Service
public class UkrainianTranslator {

    public Command getBotsCommand(String message) {
        if(message.toLowerCase(Locale.ROOT).contains("привiт")) {
            return Command.HELLO;
        }
        else if (message.toLowerCase(Locale.ROOT).contains("відгук:")) {
            return Command.FEEDBACK_MESSAGE;
        }
        else {
            return switch (message) {
                case "Так", "так" -> Command.YES;
                case "Ні", "ні" -> Command.NO;
                case "Грати з поточними налаштуваннями" -> Command.PLAY_USING_THE_CURRENT_SETTINGS;
                case "Почати нову гру" -> Command.START_NEW_GAME;
                case "Закінчити гру" -> Command.STOP_GAME;
                case "Налаштування" -> Command.SETTINGS;
                case "Залишити відгук" -> Command.INSTRUCTION;
                case "Інструкція" -> Command.SEND_FEEDBACK;
                case "Змінити налаштування" -> Command.CHANGE_SETTINGS;
                case "Мова" -> Command.LANGUAGE;
                case "Русский язык" -> Command.RUSSIAN;
                case "English language" -> Command.ENGLISH;
                case "Українська мова" -> Command.UKRAINIAN;
                case "Режим гри" -> Command.GAME_MODE;
                case "Старий Заповіт \uD83D\uDD4E" -> Command.OLD_TESTAMENT;
                case "Новий Заповіт ✝️️" -> Command.NEW_TESTAMENT;
                case "Уся Біблія \uD83D\uDCD6" -> Command.ALL_BIBLE;
                case "Різдвяний alias ✨" -> Command.CHRISTMAS;
                case "Тривалість раунда" -> Command.ROUND_DURATION;
                case "Штраф за невідгадане слово" -> Command.PENALTY;
                case "1 бал" -> Command.PENALTY_ONE;
                case "2 бала" -> Command.PENALTY_TWO;
                case "Без штрафу" -> Command.PENALTY_ZERO;
                case "Отримати завдання \uD83D\uDCAC" -> Command.GET_TASK;
                case "Розпочати відгадувати слова" -> Command.START_GUESSING_WORDS;
                case "Відгадали ✔️" -> Command.GUESSED;
                case "Не відгадали ❌" -> Command.NOT_GUESSED;
                default -> Command.DONT_UNDERSTAND;
            };
        }
    }

    public String translateCommandToUser(Command command) {
        return switch (command) {
            case HELLO -> "Привіт";
            case YES -> "Так";
            case NO -> "Ні";
            case PLAY_USING_THE_CURRENT_SETTINGS -> "Грати з поточними налаштуваннями";
            case START_NEW_GAME -> "Почати нову гру";
            case STOP_GAME -> "Закінчити гру";
            case SETTINGS -> "Налаштування";
            case INSTRUCTION -> "Інструкція";
            case SEND_FEEDBACK -> "Залишити відгук";
            case CHANGE_SETTINGS -> "Змінити налаштування";
            case LANGUAGE -> "Мова";
            case RUSSIAN -> "Русский язык";
            case ENGLISH -> "English language";
            case UKRAINIAN -> "Українська мова";
            case GAME_MODE -> "Режим гри";
            case OLD_TESTAMENT -> "Старий Заповіт \uD83D\uDD4E";
            case NEW_TESTAMENT -> "Новий Заповіт ✝️";
            case ALL_BIBLE -> "Уся Біблія \uD83D\uDCD6";
            case CHRISTMAS -> "Різдвяний alias ✨";
            case ROUND_DURATION -> "Тривалість раунда";
            case PENALTY -> "Штраф за невідгадане слово";
            case PENALTY_ONE -> "1 бал";
            case PENALTY_TWO -> "2 бала";
            case PENALTY_ZERO -> "Без штрафу";
            case GET_TASK -> "Отримати завдання \uD83D\uDCAC";
            case START_GUESSING_WORDS -> "Розпочати відгадувати слова";
            case GUESSED -> "Відгадали ✔️";
            case NOT_GUESSED -> "Не відгадали ❌";
            default -> "";
        };
    }

    public String translateAnswersMessage(AnswersMessage message) {
        return switch (message) {
            case INSTRUCTION -> """
                        За допомогою цього бота можна грати у "Біблейський alias", використовуючи смартфон 📱замість карт. 
                        Для гри потрібно зібрати мінімально 4-и людини, поділити їх на команди та грати по відомим правилам гри в alias. 
                        У розділі "Настройки" можна обрати режим гри (Старий Заповіт, Новий Заповіт, уся Біблія чи Різдвяний аlias), тривалість раунда та штраф за невідгадане слово. 
                        Нинішні налаштування нагадуються на початку кожної нової гри. Також на початку гри необхідно ввести назви команд, за допомогою яких бот буде контролювати чергу хода. 
                        Відлік часу раунда починаєтся з того моменту, коли було отримано перше слово для відгадування. 
                        Бажаємо гарного часу разом! \uD83D\uDE07
                        """;
            case ENTER_TEAM_NAMES -> "Введіть назви команд через кому. Скільки назв запишете, стільки й буде команд у грі.";
            case SELECT_GAME_MODE -> "Оберіть режим гри";
            case SELECT_ROUND_DURATION -> "Оберіть чи введіть свою тривалість раунда ⏳ (у секундах)";
            case HOW_MUCH_POINTS_OF_PENALTY -> "На скільки балів штрафувати команду за невідгадане слово?";
            case ROUND_DURATION_NOT_LESS -> "Тривалість раунда не може бути менше 15 секунд!";
            case ROUND_DURATION_NOT_MORE -> "Тривалість раунда не може бути більше 3 хвилин!";
            case WANT_TO_EXIT_THE_SETTINGS -> "Хочете вийти з налаштувань?";
            case WHAT_TO_CHANGE -> "Що хочете змінити?";
            case CHOOSE_LANGUAGE -> "Оберіть мову";
            case NOW_YOU_CAN_PLAY -> "Тепер можна зібрати друзів та пограти \uD83D\uDE07";
            case SUCCESSFUL_AUTHORIZATION -> "Ваша авторизація пройшла з успіхом! \uD83D\uDC4D" +
                    "Тепер Ви можете грати у \"Біблейський alias\" за допомогою цього боту. " +
                    "Внизу сліва є меню. Оберіть там \"Начать новую игру\" та грайте, вважаючи на вказівки бота \uD83E\uDD16 " +
                    "Для закінчення гри оберіть у меню \"Завершить игру\" чи напишіть боту \"Закінчити гру\"";
            case INVALID_CODE -> "Недійсний код авторизації";
            case YOU_DO_NOT_HAVE_ACCESS_TO_THE_BOT -> "На жаль, у Вас немає доступа до бота \uD83D\uDE15 " +
                    "Щоб грати у \"Біблейський alias\", купіть доступ, натиснувши на посилання до нашого магазину: " +
                    "https://biblealias.ru/products/Bibleysky-alias-Bot-p406365873" +
                    "\nЯкщо у Вас вже є код авторизації (номер Вашого замовлення у форматі #*****), відправьте його у повідомленні.";
            case WELCOME_MESSAGE -> """
                Привіт! \uD83D\uDC4B\s
                Ласкаво просимо в чат-бот гри "Біблейський alias"!
                За допомогою цього боту можно грати в нашу гру, використовуючи смартфон \uD83D\uDCF1 замість карт.
                Для отримання доступу до бота пройдіть по посиланню в наш магазин та оформіть підписку:
                https://biblealias.ru/products/Bibleysky-alias-Bot-p406365873
                Якщо у Вас вже є код авторизації (номер вашого замовлення у форматі #*****), відправьте його в повідомленні.
                """;
            case CANNOT_STOP_GAME_WITHOUT_STARTING -> "Ви не можете закінчити гру не розпочавши \uD83D\uDE09";
            case WINNER_TEAM -> "Перемогла команда ";
            case WINNER_TEAMS -> "Перемогли команди: ";
            case DRAW -> "Ничья! \uD83D\uDC4D";
            case NUMBER_OF_POINTS -> "Кількість гравців";
            case HELLO_TEAMS -> "Привіт \uD83D\uDC4B командам ";
            case YOUR_ROUND_DURATION_IS -> "Тривалість вашого раунда буде";
            case IT_IS_TIME_FOR_EVERY_TEAM -> " секунд — це час, який по черзі дається кожній команді на пояснення та відгадування слів. "
                    + "Коли час закінчується, хід переходить іншій команді.";
            case SO_FIRST_TEAM_IS -> "Першою починає команда ";
            case GIVE_THEM_PHONE -> "Передайте їм телефон \uD83D\uDCF2";
            case ARE_YOU_READY_TO_START -> ", ви готови розпочати? Якщо так, натисніть \"Отримати завдання\". "
                    + "Прочитайте його вголос, а потім почніть відгадувати слова \uD83D\uDE09";

            case TASK_IS -> "Завдання";
            case TURNS_TEAM_IS -> "Черга команди";
            case WORD_IS -> "Слово";

            case TIME_OF_TEAM -> "Час команди ";
            case IS_OVER -> " вийшов! ⏳";
            case TURN_GOES_TO_TEAM -> "Черга переходить команді: ";

            case SORRY_I_DONT_UNDERSTAND -> "Пробачте, я Вас не розумію";
            case TRY_NEW_GAME -> "Спробуйте розпочати нову гру \uD83E\uDD17";

            case THANKS_FOR_FEEDBACK -> "Дякуємо за зворотній зв'язок \uD83D\uDE0A";
            case FEEDBACK_INSTRUCTION -> "Ми будемо дуже вдячні отримати відгук чи побажання від Вас \uD83D\uDCAC " +
                    "Для цього відправьте повідомлення: \"Відгук: ... \" та ми його обов'язково прочитаємо \uD83E\uDD17";
        };
    }

}
