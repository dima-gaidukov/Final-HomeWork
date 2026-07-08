package dev.sorokin.eventnotificator.repository;

import dev.sorokin.eventnotificator.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdAndIsReadFalse(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true," +
            " n.readAt = :now WHERE n.id IN (:ids) AND n.userId = :userId")
    int markAsRead(@Param("userId")Long userId,
                   @Param("ids") List<Long> ids,
                   @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.createdAt < :threshold")
    int deleteOldNotifications(@Param("threshold") LocalDateTime threshold);

}
