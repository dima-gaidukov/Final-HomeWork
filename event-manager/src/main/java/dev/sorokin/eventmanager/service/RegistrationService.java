package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.domain.EventStatus;
import dev.sorokin.eventmanager.entity.RegistrationEntity;
import dev.sorokin.eventmanager.exception.ResourceNotFoundException;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.RegistrationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    public RegistrationService(RegistrationRepository registrationRepository,EventRepository eventRepository,EventMapper eventMapper) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public void registerForEvent (Long userId, Long eventId) {

        var event = eventRepository.findById(eventId)
                .orElseThrow(()-> new ResourceNotFoundException("Event not found"));
        if(event.getStatus() != EventStatus.WAIT_START) {
            throw new IllegalArgumentException("Registration not possible status is incorrect");
        }
        if(event.getOccupiedPlaces() >= event.getMaxPlaces()) {
            throw new IllegalArgumentException("Max places exceeded");
        }
        if(registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("You by registory");
        }

        RegistrationEntity regEntity = new RegistrationEntity(null,eventId,userId);
        registrationRepository.save(regEntity);
        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
        eventRepository.save(event);

    }

    @Transactional
    public void cancelRegistration(Long userId, Long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(()-> new ResourceNotFoundException("Event not found"));
        if(event.getStatus() != EventStatus.WAIT_START) {
            throw new IllegalArgumentException("Registration not possible status is incorrect");
        }

        RegistrationEntity reg = registrationRepository.findByEventIdAndUserId(eventId,userId)
                .orElseThrow(()-> new ResourceNotFoundException("Registration not found"));
        registrationRepository.delete(reg);
        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);

    }

    public List<Event> getMyRegistrations(Long userId){

        return registrationRepository.findByUserId(userId)
                .stream()
                .map(reg -> eventRepository.findById(reg.getEventId()).orElseThrow(()-> new ResourceNotFoundException("Not found")))
                .map(eventMapper::toDomain)
                .toList();


    }


}
