package ru.biblealias.services;

import org.springframework.stereotype.Service;
import ru.biblealias.models.Language;
import ru.biblealias.models.TasksMdl;
import ru.biblealias.repositories.TasksRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TasksService {
    private final TasksRepository tasksRepository;

    public TasksService(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    public TasksMdl saveTask(TasksMdl task) {
        return tasksRepository.save(task);
    }

    public List<TasksMdl> getAllTasks() {
        return tasksRepository.findAll();
    }

    public List<TasksMdl> getOldTestamentTasks() {
        return tasksRepository.getAllByMode("old");
    }

    public List<TasksMdl> getNewTestamentTasks() {
        return tasksRepository.getAllByMode("new");
    }

    public List<TasksMdl> getAllBibleTasks() {
        List<TasksMdl> allBibleTasks = new ArrayList<>();
        allBibleTasks.addAll(tasksRepository.getAllByMode("old"));
        allBibleTasks.addAll(tasksRepository.getAllByMode("new"));
        return allBibleTasks;
    }

    public List<TasksMdl> getChristmasTasks(Language language) {
        return switch (language) {
            case RUSSIAN -> tasksRepository.getAllByMode("Christmas");
            case UKRAINIAN -> tasksRepository.getAllByMode("Christmas_Ucr");
            case ENGLISH -> tasksRepository.getAllByMode("Christmas");
        };
    }
}
