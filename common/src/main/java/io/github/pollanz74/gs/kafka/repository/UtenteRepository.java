package io.github.pollanz74.gs.kafka.repository;

import io.github.pollanz74.gs.kafka.entity.UtenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteRepository extends JpaRepository<UtenteEntity, Long> {
}
