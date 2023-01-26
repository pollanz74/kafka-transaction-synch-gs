package io.github.pollanz74.gs.kafka.service.support;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
public class UtenteValidationResultCallback implements ListenableFutureCallback<SendResult<Long, UtenteEntity>> {

    @Override
    public void onFailure(Throwable ex) {
        log.error("Error", ex);
    }

    @Override
    public void onSuccess(SendResult<Long, UtenteEntity> result) {
        if (result != null) {
            log.info("Sent({}): {}", result.getProducerRecord().key(), result.getProducerRecord().value());
        }
    }

}
