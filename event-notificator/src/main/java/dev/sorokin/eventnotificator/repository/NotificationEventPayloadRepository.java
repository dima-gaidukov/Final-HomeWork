package dev.sorokin.eventnotificator.repository;

import dev.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface NotificationEventPayloadRepository extends JpaRepository<NotificationEventPayloadEntity, Long> {

    boolean existsByMessageId(UUID messageId);

    @Modifying
    @Query("DELETE FROM NotificationEventPayloadEntity p WHERE p.id NOT IN (SELECT n.payload.id FROM NotificationEntity n)")
    int deleteOrphanPayloads();

}
