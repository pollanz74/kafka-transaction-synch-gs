package io.github.pollanz74.gs.kafka;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import io.github.pollanz74.gs.kafka.utils.TopicUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.sql.DataSource;

@Slf4j
@EnableKafka
@SpringBootApplication
public class ForwardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForwardServiceApplication.class, args);
    }

    @Bean
    NewTopic utentiActivationTopic() {
        return TopicBuilder.name(TopicUtils.TOPIC_NAME_USER_ACTIVATION)
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
