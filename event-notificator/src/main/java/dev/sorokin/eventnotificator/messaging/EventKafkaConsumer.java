package dev.sorokin.eventnotificator.messaging;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventcommon.kafka.EventChangeKafkaMessage;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;
import dev.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import dev.sorokin.eventnotificator.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class EventKafkaConsumer {

    private final NotificationEventPayloadRepository payloadRepository;

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;


    public EventKafkaConsumer(NotificationEventPayloadRepository payloadRepository, NotificationRepository notificationRepository, ObjectMapper objectMapper) {
        this.payloadRepository = payloadRepository;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "event-changes")
    public void consume(EventChangeKafkaMessage message) throws JsonProcessingException {

        if(payloadRepository.existsByMessageId(message.getMessageId())) {
            return;
        }
        var mes =  new NotificationEventPayloadEntity();
        mes.setMessageId(message.getMessageId());
        mes.setEventType(message.getEventType());
        mes.setEventId(message.getEventId());
        mes.setOccurredAt(message.getOccurredAt());
        mes.setOwnerId(message.getOwnerId());
        mes.setChangedById(message.getChangedById());

        var payLoadMap = new HashMap<String, Object>();
        payLoadMap.put("eventName", message.getEventName());
        payLoadMap.put("changedById", message.getChangedById());
        payLoadMap.put("changes", message.getChanges());
        mes.setPayload(objectMapper.writeValueAsString(payLoadMap));

        payloadRepository.save(mes);

        for (Long userId : message.getSubscribers()){
            var user = new NotificationEntity();
            user.setUserId(userId);
            user.setRead(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setPayload(mes);
            notificationRepository.save(user);
        }





    }
}
