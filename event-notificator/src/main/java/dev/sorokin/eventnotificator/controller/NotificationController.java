package dev.sorokin.eventnotificator.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventnotificator.dto.MarkNotificationsAsReadRequest;
import dev.sorokin.eventnotificator.dto.NotificationPayloadDto;
import dev.sorokin.eventnotificator.dto.NotificationResponseDto;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.repository.NotificationRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private static  final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public NotificationController(NotificationRepository notificationRepository,
                                  ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;

        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<NotificationResponseDto>> getNotifications() {

        log.info("Getting notifications");

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<NotificationEntity> entities = notificationRepository.findByUserIdAndIsReadFalse(userId);

        List<NotificationResponseDto> result = new ArrayList<>();

        for (NotificationEntity entity : entities) {

            try{
                NotificationPayloadDto payLoadDto = objectMapper.readValue(entity.getPayload()
                        .getPayload(),NotificationPayloadDto.class);

                payLoadDto.setMessageId(entity.getPayload().getMessageId());
                payLoadDto.setEventType(entity.getPayload().getEventType());
                payLoadDto.setOccurredAt(entity.getPayload().getOccurredAt());
                payLoadDto.setOwnerId(entity.getPayload().getOwnerId());

                NotificationResponseDto notificationResponseDto = new NotificationResponseDto(
                        entity.getId(),
                        entity.getPayload().getEventType(),
                        entity.getPayload().getEventId(),
                        entity.getCreatedAt(),
                        entity.getRead(),
                        "Событие «" + payLoadDto.getEventName() + "» было изменено",
                        payLoadDto
                );

                result.add(notificationResponseDto);
            }catch (JsonProcessingException e){
                log.error(e.getMessage());
            }

        }

        return ResponseEntity.ok(result);

    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> markAsRead(@RequestBody MarkNotificationsAsReadRequest request) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        notificationRepository.markAsRead(
                userId,
                request.getNotificationIds(),
                LocalDateTime.now()
        );

        try {
            long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
            stringRedisTemplate.opsForValue().set("notif:unread:" + userId, String.valueOf(count));
        }catch (Exception e){
            log.error("Redis error", e);
        }

        return ResponseEntity.noContent().build();

    }

}
