package ru.biblealias.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.biblealias.models.Cheque;
import java.util.Optional;

public interface ChequesRepository  extends JpaRepository<Cheque, Integer> {
    Optional<Cheque> findByChequeNumber(String chequeNumber);
}
