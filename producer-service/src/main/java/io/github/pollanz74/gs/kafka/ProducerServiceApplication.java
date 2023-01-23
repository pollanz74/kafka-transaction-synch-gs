package io.github.pollanz74.gs.kafka;

import com.github.javafaker.Faker;
import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import io.github.pollanz74.gs.kafka.service.UtenteService;
import io.github.pollanz74.gs.kafka.utils.TopicUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.sql.DataSource;
import java.util.*;

@Slf4j
@SpringBootApplication
public class ProducerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(Faker faker, UtenteService utenteService) {
        return (args) -> {

            Collection<UtenteEntity> utenti = new HashSet<>();

            for (int i = 0; i < 10; i++) {
                UtenteEntity utente =
                        utenteService.createUtente(
                                faker.name().firstName(),
                                faker.name().lastName(),
                                faker.date().birthday(18, 99),
                                RandomStringUtils.randomAlphanumeric(16).toUpperCase()
                        );
                utenti.add(utente);
                log.debug("Creato utente {}", utente);
            }

        };
    }

    @Bean
    Faker faker() {
        return new Faker(Locale.ITALIAN);
    }

    @Bean
    NewTopic utentiTopic() {
        return TopicBuilder.name(TopicUtils.TOPIC_NAME)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    JpaTransactionManager transactionManager(DataSource dataSource) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setDataSource(dataSource);
        return jpaTransactionManager;
    }

    @Bean
    ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager(
            KafkaTransactionManager<Long, UtenteEntity> kafkaTransactionManager,
            JpaTransactionManager jpaTransactionManager) {
        return new ChainedKafkaTransactionManager<>(kafkaTransactionManager, jpaTransactionManager);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory,
            ChainedKafkaTransactionManager<Object, Object> chainedTM) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        factory.getContainerProperties().setTransactionManager(chainedTM);
        return factory;
    }

}
