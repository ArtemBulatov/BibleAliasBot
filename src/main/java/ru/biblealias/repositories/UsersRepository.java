package ru.biblealias.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.biblealias.models.UsersMdl;
import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<UsersMdl, UUID> {
    Optional<UsersMdl> findByChatId(long chatId);
    Optional<UsersMdl> findByUserName(String userName);
}
