package ru.biblealias.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@Table(name = "words")
public class WordsMdl {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "word")
    @NotEmpty
    private String word;

    @Column(name = "mode")
    private String mode;
}
