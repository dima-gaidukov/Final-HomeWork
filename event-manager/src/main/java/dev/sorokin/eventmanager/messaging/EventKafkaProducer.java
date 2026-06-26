package dev.sorokin.eventmanager.messaging;

import dev.sorokin.eventcommon.kafka.EventChangeKafkaMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class EventKafkaProducer {

    private final KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate;

    private final String topic;


    public EventKafkaProducer(KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate,
                              @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishEventChange(EventChangeKafkaMessage message) {
        message.setMessageId(UUID.randomUUID());
        kafkaTemplate.send(topic, message);

    }
}
