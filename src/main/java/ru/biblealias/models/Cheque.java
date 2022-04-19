package ru.biblealias.models;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "cheques")
public class Cheque {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cheque_number")
    private String chequeNumber;

    public Cheque(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public Cheque() {

    }
}
