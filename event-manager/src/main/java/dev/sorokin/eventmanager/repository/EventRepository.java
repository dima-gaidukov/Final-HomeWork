package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.domain.EventStatus;
import dev.sorokin.eventmanager.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {

    List<EventEntity> findByOwnerId(Long ownerId);

    List<EventEntity> findByLocationId(Long locationId);

    List<EventEntity> findByLocationIdAndMaxPlacesGreaterThan(Long locationId, Integer capacity);

    List<EventEntity> findByStatus(EventStatus status);



}
