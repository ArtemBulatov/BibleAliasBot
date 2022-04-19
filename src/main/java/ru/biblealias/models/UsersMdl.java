package ru.biblealias.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class UsersMdl {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "chat_id", unique = true)
    @NotEmpty
    private long chatId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "games_count")
    private Integer gamesCount;

    @Column(name = "is_authorized")
    private boolean isAuthorized;

    @Column(name = "cheque")
    private String cheque;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "settings")
    private String settings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersMdl usersMdl = (UsersMdl) o;

        if (id != null ? !id.equals(usersMdl.id) : usersMdl.id != null) return false;
        if (chatId != usersMdl.chatId) return false;
        if (isAuthorized != usersMdl.isAuthorized) return false;
        if (userName != null ? !userName.equals(usersMdl.userName) : usersMdl.userName != null) return false;
        if (firstName != null ? !firstName.equals(usersMdl.firstName) : usersMdl.firstName != null) return false;
        if (lastName != null ? !lastName.equals(usersMdl.lastName) : usersMdl.lastName != null) return false;
        if (date != null ? !date.equals(usersMdl.date) : usersMdl.date != null) return false;
        if (settings != null ? !settings.equals(usersMdl.settings) : usersMdl.settings != null) return false;
        return gamesCount != null ? gamesCount.equals(usersMdl.gamesCount) : usersMdl.gamesCount == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (chatId ^ (chatId >>> 32));
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (gamesCount != null ? gamesCount.hashCode() : 0);
        result = 31 * result + (isAuthorized ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        String isAuthorizedText;
        if (isAuthorized()) {
            isAuthorizedText = "Авторизован";
        }
        else {
            isAuthorizedText = "Не авторизован";
        }

        return userName + ", "
                + firstName + " "
                + lastName
                + ", завершённых игр: " + gamesCount.toString()
                + ". " + isAuthorizedText
                + "Чек: " + cheque;
    }
}
