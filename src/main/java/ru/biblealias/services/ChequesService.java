package ru.biblealias.services;

import org.springframework.stereotype.Service;
import ru.biblealias.models.Cheque;
import ru.biblealias.repositories.ChequesRepository;

@Service
public class ChequesService {
    private final ChequesRepository chequesRepository;

    public ChequesService(ChequesRepository chequesRepository) {
        this.chequesRepository = chequesRepository;
    }

    public Cheque addNewCheque(String chequeNumber) {
        return chequesRepository.save(new Cheque(chequeNumber));
    }

    public Cheque findByChequeNumber(String chequeNumber) {
        return chequesRepository.findByChequeNumber(chequeNumber).orElse(null);
    }

    public void deleteCheque(Cheque cheque) {
        chequesRepository.delete(cheque);
    }
}
