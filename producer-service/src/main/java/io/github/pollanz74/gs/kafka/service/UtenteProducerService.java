package io.github.pollanz74.gs.kafka.service;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import io.github.pollanz74.gs.kafka.service.support.UtenteValidationResultCallback;
import io.github.pollanz74.gs.kafka.utils.TopicUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UtenteProducerService {

    private final KafkaTemplate<Long, UtenteEntity> kafkaTemplate;
    private final UtenteValidationResultCallback callback;

    //@Transactional("kafkaTransactionManager") se presente committa prima la transazione su kafka
    public void sendUtenteForActivation(Collection<UtenteEntity> utenti) {
        for (UtenteEntity utente : utenti) {
            ListenableFuture<SendResult<Long, UtenteEntity>> result = kafkaTemplate.send(TopicUtils.TOPIC_NAME_USER_VALIDATION, utente.getIdUtente(), utente);
            result.addCallback(callback);
        }
    }

}
