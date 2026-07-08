package dev.sorokin.eventnotificator.messaging;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventcommon.kafka.EventChangeKafkaMessage;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;
import dev.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import dev.sorokin.eventnotificator.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Component
public class EventKafkaConsumer {

    private final NotificationEventPayloadRepository payloadRepository;

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;


    public EventKafkaConsumer(NotificationEventPayloadRepository payloadRepository,
                              NotificationRepository notificationRepository,
                              ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
        this.payloadRepository = payloadRepository;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;

        this.stringRedisTemplate = stringRedisTemplate;
    }

    @KafkaListener(topics = "event-changes")
    public void consume(EventChangeKafkaMessage message) throws JsonProcessingException {

        if(payloadRepository.existsByMessageId(message.getMessageId())) {
            return;
        }
        var payloadEntity =  new NotificationEventPayloadEntity();
        payloadEntity.setMessageId(message.getMessageId());
        payloadEntity.setEventType(message.getEventType());
        payloadEntity.setEventId(message.getEventId());
        payloadEntity.setOccurredAt(message.getOccurredAt());
        payloadEntity.setOwnerId(message.getOwnerId());
        payloadEntity.setChangedById(message.getChangedById());

        var payLoadMap = new HashMap<String, Object>();
        payLoadMap.put("eventName", message.getEventName());
        payLoadMap.put("changedById", message.getChangedById());
        payLoadMap.put("changes", message.getChanges());
        payloadEntity.setPayload(objectMapper.writeValueAsString(payLoadMap));

        payloadRepository.save(payloadEntity);

        for (Long userId : message.getSubscribers()){
            var notificationEntity = new NotificationEntity();
            notificationEntity.setUserId(userId);
            notificationEntity.setRead(false);
            notificationEntity.setCreatedAt(LocalDateTime.now());
            notificationEntity.setPayload(payloadEntity);
            notificationRepository.save(notificationEntity);

            try {
                stringRedisTemplate.opsForValue().increment("notif:unread:" + userId);

            } catch (Exception e) {
                log.error("Redis error", e);
            }

        }





    }
}
