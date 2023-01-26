package io.github.pollanz74.gs.kafka.service;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import io.github.pollanz74.gs.kafka.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public Optional<UtenteEntity> validateUtente(UtenteEntity utente) {
        if (utenteRepository.findById(utente.getIdUtente()).isPresent()) {
            utente.setValido(true);
            return Optional.of(utenteRepository.save(utente));
        }

        log.warn("Utente non trovato: ", utente);
        return Optional.empty();
    }

}
