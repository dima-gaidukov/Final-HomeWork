package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.EventStatus;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class EventSchedulerService {

    private final EventRepository eventRepository;

    public EventSchedulerService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void processStatusUpdate() {

        List<EventEntity> starter = eventRepository.findByStatus(EventStatus.WAIT_START);
        for (EventEntity event : starter) {
            if(event.getDate().isBefore(LocalDateTime.now())) {
                event.setStatus(EventStatus.STARTED);

            }
        }
        eventRepository.saveAll(starter);

        List<EventEntity> finished = eventRepository.findByStatus(EventStatus.STARTED);
        for(EventEntity event : finished) {
            if(event.getDate().plusMinutes(event.getDuration()).isBefore(LocalDateTime.now())) {
                event.setStatus(EventStatus.FINISHED);

            }
        }
        eventRepository.saveAll(finished);

    }
}
