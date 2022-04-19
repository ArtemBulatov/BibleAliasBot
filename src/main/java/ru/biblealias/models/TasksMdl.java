package ru.biblealias.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@Table(name = "tasks")
public class TasksMdl {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "task")
    @NotEmpty
    private String task;

    @Column(name = "mode")
    private String mode;
}
