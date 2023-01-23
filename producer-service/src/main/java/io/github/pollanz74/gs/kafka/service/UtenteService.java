package io.github.pollanz74.gs.kafka.service;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import io.github.pollanz74.gs.kafka.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepository utenteRepository;

    private final UtenteProducerService utenteProducerService;

    @Transactional("chainedKafkaTransactionManager")
    public UtenteEntity createUtente(String nome, String cognome, Date dataNascita, String codiceFiscale) {
        UtenteEntity utente = utenteRepository.save(
                UtenteEntity
                        .builder()
                        .nome(nome)
                        .cognome(cognome)
                        .dataNascita(dataNascita)
                        .cf(codiceFiscale)
                        .dataAggiornamento(new Date())
                        .build()
        );

        utenteProducerService.sendUtenteForActivation(Set.of(utente));

        return utente;
    }

}
