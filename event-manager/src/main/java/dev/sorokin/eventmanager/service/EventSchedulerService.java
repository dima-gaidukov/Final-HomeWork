package dev.sorokin.eventmanager.service;

import dev.sorokin.eventcommon.kafka.ChangeItem;
import dev.sorokin.eventcommon.kafka.EventChangeKafkaMessage;
import dev.sorokin.eventmanager.domain.EventStatus;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.RegistrationEntity;
import dev.sorokin.eventmanager.messaging.EventKafkaProducer;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.RegistrationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class EventSchedulerService {

    private final EventRepository eventRepository;

    private final RegistrationRepository registrationRepository;

    private final EventKafkaProducer eventKafkaProducer;


    public EventSchedulerService(EventRepository eventRepository, RegistrationRepository registrationRepository, EventKafkaProducer eventKafkaProducer) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.eventKafkaProducer = eventKafkaProducer;
    }

    @Scheduled(fixedRate = 60000)
    public void processStatusUpdate() {

        List<EventEntity> starter = eventRepository.findByStatus(EventStatus.WAIT_START);

        List<EventEntity> newListStarter = new ArrayList<>();

        for (EventEntity event : starter) {
            if(event.getDate().isBefore(LocalDateTime.now())) {
                event.setStatus(EventStatus.STARTED);
                newListStarter.add(event);
            }
        }
        var starterNew = eventRepository.saveAll(starter);

        for (EventEntity event : newListStarter) {
            var subscribersIds = registrationRepository.findByEventId(event.getId())
                    .stream()
                    .map(RegistrationEntity::getUserId)
                    .toList();
            List<ChangeItem> chang = List.of(new ChangeItem("status", "WAIT_START", "STARTED"));
            eventKafkaProducer.publishEventChange(new EventChangeKafkaMessage(
                    null,
                    "EVENT_STATUS_CHANGED",
                    event.getId(),
                    event.getName(),
                    LocalDateTime.now(),
                    event.getOwnerId(),
                    null,
                    subscribersIds,
                    chang));

        }

        List<EventEntity> newListFinished = new ArrayList<>();

        List<EventEntity> finished = eventRepository.findByStatus(EventStatus.STARTED);
        for(EventEntity event : finished) {
            if(event.getDate().plusMinutes(event.getDuration()).isBefore(LocalDateTime.now())) {
                event.setStatus(EventStatus.FINISHED);
                newListFinished.add(event);
            }
        }
        var finishedNew = eventRepository.saveAll(finished);

        for (EventEntity event : newListFinished) {
            var subscribersIds = registrationRepository.findByEventId(event.getId())
                    .stream()
                    .map(RegistrationEntity::getUserId)
                    .toList();
            List<ChangeItem> chang = List.of(new ChangeItem("status", "STARTED", "FINISHED"));

            eventKafkaProducer.publishEventChange(new EventChangeKafkaMessage(null,
                    "EVENT_STATUS_CHANGED",
                    event.getId(),
                    event.getName(),
                    LocalDateTime.now(),
                    event.getOwnerId(),
                    null,
                    subscribersIds,
                    chang));
        }

    }
}
