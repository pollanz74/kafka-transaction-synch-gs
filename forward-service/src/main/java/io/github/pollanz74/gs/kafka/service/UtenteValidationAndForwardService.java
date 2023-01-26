package io.github.pollanz74.gs.kafka.service;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import io.github.pollanz74.gs.kafka.service.support.UtenteActivationResultCallback;
import io.github.pollanz74.gs.kafka.utils.TopicUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import static io.github.pollanz74.gs.kafka.utils.TopicUtils.TOPIC_NAME_USER_ACTIVATION;
import static io.github.pollanz74.gs.kafka.utils.TopicUtils.TOPIC_NAME_USER_VALIDATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class UtenteValidationAndForwardService {

    private final UtenteService utenteService;

    private final KafkaTemplate<Long, UtenteEntity> kafkaTemplate;

    private final UtenteActivationResultCallback callback;

    @KafkaListener(
            topics = TOPIC_NAME_USER_VALIDATION,
            concurrency = "3")
    @Transactional("chainedKafkaTransactionManager")
    public void listen(UtenteEntity utente) {
        log.info("{}", utente);

        // aggiornamento su database che randomicamente fallisce
        utente = utenteService.validateUtente(utente).orElseThrow(() -> {
            log.error("Utente non valido!");
            return new RuntimeException("Utente non valido!");
        });
        log.info("utente valido: {}", utente);

        // mettiamo su topic attivazione
        ListenableFuture<SendResult<Long, UtenteEntity>> result = kafkaTemplate.send(TOPIC_NAME_USER_ACTIVATION, utente.getIdUtente(), utente);
        result.addCallback(callback);

    }

}
