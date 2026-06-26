package dev.sorokin.eventnotificator.service;



import dev.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import dev.sorokin.eventnotificator.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CleanupService {

    private static  final Logger log = LoggerFactory.getLogger(CleanupService.class);

    private final NotificationRepository notificationRepository;

    private final NotificationEventPayloadRepository payloadRepository;

    public CleanupService(NotificationRepository notificationRepository,
                          NotificationEventPayloadRepository payloadRepository) {
        this.notificationRepository = notificationRepository;
        this.payloadRepository = payloadRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanOldNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        int deletedNotifications = notificationRepository.deleteOldNotifications(threshold);
        int deletedPayloads = payloadRepository.deleteOrphanPayloads();
        log.info("Cleaned {} notifications and {} orphan payloads", deletedNotifications, deletedPayloads);
    }
}
