package ru.biblealias.services;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.biblealias.models.Settings;
import ru.biblealias.models.UsersMdl;
import ru.biblealias.repositories.UsersRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final Gson gson;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        gson = new Gson();
    }

    public UsersMdl createUser(UsersMdl user){
        user.setId(UUID.randomUUID());
        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        user.setRegDate(LocalDateTime.now(zoneId));
        user.setDate(LocalDateTime.now(zoneId));
        return usersRepository.save(user);
    }

    public List<UsersMdl> getAllUsers() {
        return usersRepository.findAll();
    }

    public UsersMdl getUser(UUID id) {
        return usersRepository.getById(id);
    }

    public UsersMdl updateUserByChatId (long chatId, UsersMdl updatedUser) {
        return updateUser(findUserByChatId(chatId), updatedUser);
    }

    public UsersMdl updateUser(UsersMdl user, UsersMdl updatedUser) {
        if (usersRepository.existsById(user.getId())) {
            UsersMdl newUser = usersRepository.getById(user.getId());

            if(updatedUser.getUserName() != null && !updatedUser.getUserName().equals(newUser.getUserName())){
                newUser.setUserName(updatedUser.getUserName());
            }
            if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().equals(newUser.getFirstName())){
                newUser.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null && !updatedUser.getLastName().equals(newUser.getLastName())){
                newUser.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getGamesCount() != null && !updatedUser.getGamesCount().equals(newUser.getGamesCount())){
                newUser.setGamesCount(updatedUser.getGamesCount());
            }
            if (updatedUser.getSettings() != null && !updatedUser.getSettings().equals(newUser.getSettings())) {
                newUser.setSettings(updatedUser.getSettings());
            }

            if(!newUser.equals(user)){
                ZoneId zoneId = ZoneId.of("Europe/Moscow");
                newUser.setDate(LocalDateTime.now(zoneId));
                return usersRepository.save(newUser);
            }
        }
        return updatedUser;
    }

    public UsersMdl findUserByChatId (long chatId) {
        return usersRepository.findByChatId(chatId).stream().findAny().orElse(null);
    }

    public UsersMdl findUserByUserName(String userName) {
        return usersRepository.findByUserName(userName).stream().findAny().orElse(null);
    }

    public boolean deleteUser(UUID id) {
        if (usersRepository.existsById(id)) {
            usersRepository.deleteById(id);
            return true;
        }
        return  false;
    }

    public UsersMdl setAuthorization(String userName) {
        UsersMdl user = findUserByUserName(userName);
        if (user != null) {
            user.setAuthorized(true);
            return usersRepository.save(user);
        }
        else return new UsersMdl();
    }

    public UsersMdl setAuthorization(long chatId, String cheque) {
        UsersMdl user = findUserByChatId(chatId);
        if (user != null) {
            user.setAuthorized(true);
            user.setCheque(cheque);
            return usersRepository.save(user);
        }
        else return new UsersMdl();
    }

    public Set<Long> getAuthorizedChatIdSet() {
        Set<Long> chatIdSet = new HashSet<>();
        getAllUsers().forEach(user -> {
            if (user.isAuthorized()){
                chatIdSet.add(user.getChatId());
            }
        });
        return chatIdSet;
    }

    public Set<Long> getNotAuthorizedChatIdSet() {
        Set<Long> chatIdSet = new HashSet<>();
        getAllUsers().forEach(user -> {
            if (!user.isAuthorized()){
                chatIdSet.add(user.getChatId());
            }
        });
        return chatIdSet;
    }

    public Set<Long> getUsersWhoCanPlayTrialGame() {
        Set<Long> chatIdSet = new HashSet<>();
        getAllUsers().forEach(user -> {
            if (!user.isAuthorized() && user.getGamesCount() < 1){
                chatIdSet.add(user.getChatId());
            }
        });
        return chatIdSet;
    }

    public UsersMdl createOrUpdateUser(Chat chat) {
        UsersMdl user = findUserByChatId(chat.getId());
        if (user == null) {
            UsersMdl newUser = new UsersMdl();
            newUser.setChatId(chat.getId());
            newUser.setUserName(checkNull(chat.getUserName()));
            newUser.setFirstName(checkNull(chat.getFirstName()));
            newUser.setLastName(checkNull(chat.getLastName()));
            newUser.setGamesCount(0);
            return createUser(newUser);
        }
        else {
            UsersMdl updatedUser = new UsersMdl();
            updatedUser.setUserName(checkNull(chat.getUserName()));
            updatedUser.setFirstName(checkNull(chat.getFirstName()));
            updatedUser.setLastName(checkNull(chat.getLastName()));
            updateUser(user, updatedUser);
            return null;
        }
    }

    public UsersMdl setSettingsToUser(long chatId, Settings settings) {
        UsersMdl user = findUserByChatId(chatId);
        user.setSettings(gson.toJson(settings));
        return updateUserByChatId(chatId, user);
    }

    public Settings getUsersSettings(long chatId) {
        UsersMdl user = findUserByChatId(chatId);
        Settings settings = new Settings();
        if (user != null && user.getSettings() != null) {
            settings = gson.fromJson(user.getSettings(), Settings.class);
        }
        return settings;
    }

    private String checkNull(String s) {
        if (s != null) return s;
        else return "";
    }
}
