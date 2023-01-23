package io.github.pollanz74.gs.kafka.service;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static io.github.pollanz74.gs.kafka.utils.TopicUtils.TOPIC_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class UtenteConsumerService {

    private final UtenteService utenteService;

    @KafkaListener(
            topics = TOPIC_NAME,
            concurrency = "3")
    @Transactional
    public void listen(UtenteEntity utente) {
        log.info("{}", utente);
        Optional<UtenteEntity> optionalUtente = utenteService.enableUtente(utente);
        log.info("utente abilitato: {}", optionalUtente.orElse(null));
    }

}
