package ru.biblealias.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.biblealias.models.TasksMdl;
import java.util.List;

public interface TasksRepository extends JpaRepository<TasksMdl, Integer> {
    List<TasksMdl> getAllByMode(String mode);
}
