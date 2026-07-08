package dev.sorokin.eventmanager.service;


import dev.sorokin.eventcommon.kafka.ChangeItem;
import dev.sorokin.eventcommon.kafka.EventChangeKafkaMessage;
import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.domain.EventStatus;
import dev.sorokin.eventmanager.dto.EventSearchRequestDto;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.RegistrationEntity;
import dev.sorokin.eventmanager.exception.ResourceNotFoundException;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.messaging.EventKafkaProducer;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.repository.RegistrationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final LocationRepository locationRepository;

    private final RegistrationRepository registrationRepository;

    private final EventKafkaProducer eventKafkaProducer;

    public EventService(EventRepository eventRepository, EventMapper eventMapper, LocationRepository locationRepository, RegistrationRepository registrationRepository, EventKafkaProducer eventKafkaProducer) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.locationRepository = locationRepository;
        this.registrationRepository = registrationRepository;
        this.eventKafkaProducer = eventKafkaProducer;
    }

    public Event createEvent(Long userId, Event event) {

        LocationEntity locationEntity = locationRepository.findById(event.getLocationId())
                .orElseThrow(()-> new ResourceNotFoundException("Location not found"));

        if(event.getMaxPlaces() > locationEntity.getCapacity()) {
            throw new IllegalArgumentException("Max places exceeded");
        }

        if(event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date is before now");
        }

        Event upEvent = new Event(
                null,
                event.getName(),
                userId,
                event.getMaxPlaces(),
                0,
                event.getDate(),
                event.getCost(),
                event.getDuration(),
                event.getLocationId(),
                EventStatus.WAIT_START
        );

        EventEntity saveEntity = eventRepository.save(eventMapper.toEntity(upEvent));
        return eventMapper.toDomain(saveEntity);


    }

    @Cacheable(value ="events",key ="#id")
    public Event getEventById(Long id) {
        Optional<EventEntity> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            throw  new ResourceNotFoundException("Event with id " + id + " not found");
        }

        return  eventMapper.toDomain(optionalEvent.get());
    }

    @CacheEvict(value ="events",key ="#eventId")
    public Event updateEvent(Long eventId, Long userId, Event event) {

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(()-> new ResourceNotFoundException("Event with id " + eventId + " not found"));

        String oldName = eventEntity.getName();
        Integer oldCost = eventEntity.getCost();
        LocalDateTime oldDate = eventEntity.getDate();
        Integer oldDuration = eventEntity.getDuration();
        Integer oldMaxPlaces = eventEntity.getMaxPlaces();
        Long oldLocationId = eventEntity.getLocationId();

        boolean isOwner = userId.equals(eventEntity.getOwnerId());

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if(!isOwner && !isAdmin) {
            throw new AccessDeniedException("Only owner or admin can modify");
        }



        Long locationId = event.getLocationId() != null ? event.getLocationId() : eventEntity.getLocationId();
        LocationEntity location = locationRepository.findById(locationId)
                .orElseThrow(()-> new ResourceNotFoundException("Location not found"));

        if(event.getLocationId() != null) {
            eventEntity.setLocationId(event.getLocationId());
        }

        if(eventEntity.getStatus() != EventStatus.WAIT_START) {
            throw  new IllegalArgumentException("Can only modify WAIT_START events");
        }

        if(event.getMaxPlaces() != null){

            if(event.getMaxPlaces() > location.getCapacity()) {
                throw new IllegalArgumentException("Max places exceeded location capacity");
            }
            if(event.getMaxPlaces() < eventEntity.getOccupiedPlaces()) {
                throw new IllegalArgumentException("Cannot reduce maxPlaces below occupied places");
            }

            eventEntity.setMaxPlaces(event.getMaxPlaces());

        }

        if(event.getName() != null) eventEntity.setName(event.getName());
        if(event.getCost() != null) eventEntity.setCost(event.getCost());
        if(event.getDuration() != null) eventEntity.setDuration(event.getDuration());
        if(event.getDate() != null) eventEntity.setDate(event.getDate());

        EventEntity eventUpdated = eventRepository.save(eventEntity);

        List<ChangeItem> changes = new ArrayList<>();

        if(!oldName.equals(eventUpdated.getName())) {
            changes.add(new ChangeItem("name", oldName, eventUpdated.getName()));
        }
        if (!oldCost.equals(eventUpdated.getCost())) {
            changes.add(new ChangeItem("cost", oldCost, eventUpdated.getCost()));
        }
        if (!oldDate.equals(eventUpdated.getDate())) {
            changes.add(new ChangeItem("date", oldDate, eventUpdated.getDate()));
        }
        if (!oldDuration.equals(eventUpdated.getDuration())) {
            changes.add(new ChangeItem("duration", oldDuration, eventUpdated.getDuration()));
        }
        if(!oldMaxPlaces.equals(eventUpdated.getMaxPlaces())) {
            changes.add(new ChangeItem("maxPlaces", oldMaxPlaces, eventUpdated.getMaxPlaces()));
        }
        if(!oldLocationId.equals(eventUpdated.getLocationId())) {
            changes.add(new ChangeItem("locationId", oldLocationId, eventUpdated.getLocationId()));
        }

        List<RegistrationEntity> registrations = registrationRepository.findByEventId(eventId);

        List<Long> subscriberIds = registrations
                .stream()
                .map(RegistrationEntity::getUserId)
                .toList();

        eventKafkaProducer.publishEventChange(new EventChangeKafkaMessage(
                null,
                "EVENT_UPDATED",
                eventUpdated.getId(),
                eventUpdated.getName(),
                LocalDateTime.now(),
                eventUpdated.getOwnerId(),
                userId,
                subscriberIds,
                changes
        ));

        return  eventMapper.toDomain(eventUpdated);

    }

    @CacheEvict(value ="events",key ="#eventId")
    public void  deleteEvent(Long eventId, Long userId) {
        Optional<EventEntity> optionalEvent = eventRepository.findById(eventId);
        if(optionalEvent.isEmpty()) {
            throw  new ResourceNotFoundException("Event with id " + eventId + " not found");
        }

        var oldStatus = optionalEvent.get().getStatus();

        boolean isOwner = userId.equals(optionalEvent.get().getOwnerId());
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isOwner && !isAdmin) {
            throw new AccessDeniedException("Only owner or admin can delete");
        }

        if(optionalEvent.get().getStatus() != EventStatus.WAIT_START) {
            throw new IllegalArgumentException ("Can only modify WAIT_START events");
        }

        optionalEvent.get().setStatus(EventStatus.CANCELLED);
        eventRepository.save(optionalEvent.get());

        List<ChangeItem> change = List.of(new ChangeItem("status", oldStatus,optionalEvent.get().getStatus()));

        List<RegistrationEntity> registrations = registrationRepository.findByEventId(eventId);

        List<Long> subscriberIds = registrations
                .stream()
                .map(RegistrationEntity::getUserId)
                .toList();

        eventKafkaProducer.publishEventChange(new EventChangeKafkaMessage(
                null,
                "EVENT_STATUS_CHANGED",
                optionalEvent.get().getId(),
                optionalEvent.get().getName(),
                LocalDateTime.now(),
                optionalEvent.get().getOwnerId(),
                userId,
                subscriberIds,
                change
        ));

    }

    public List<Event> getMyEvents(Long ownerId) {

         return eventRepository.findByOwnerId(ownerId)
                 .stream()
                 .map(eventMapper::toDomain)
                 .toList();
    }

    public List<Event> searchEvents(EventSearchRequestDto searchRequest) {

        Specification<EventEntity> spec =
                (root, query, criteriaBuilder)
                        -> criteriaBuilder.conjunction();

        if(searchRequest.getName() != null) {

            spec = spec.and(((root, query, criteriaBuilder)
                    ->  criteriaBuilder.equal(root.get("name"), searchRequest.getName())));

        }
        if(searchRequest.getPlacesMin() != null) {

            spec = spec.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("maxPlaces"), searchRequest.getPlacesMin()));
        }
        if(searchRequest.getPlacesMax() != null) {
            spec = spec.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.lessThanOrEqualTo(root.get("maxPlaces"), searchRequest.getPlacesMax()));
        }
        if(searchRequest.getLocationId() != null) {
            spec = spec.and(((root, query, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("locationId"), searchRequest.getLocationId()) ));
        }
        if(searchRequest.getEventStatus() != null) {
            spec = spec.and(((root, query, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("status"), searchRequest.getEventStatus()) ));
        }
        if(searchRequest.getCostMax() != null) {
            spec = spec.and(((root, query, criteriaBuilder)
                    -> criteriaBuilder.lessThanOrEqualTo(root.get("cost"), searchRequest.getCostMax()) ));
        }
        if(searchRequest.getCostMin() != null) {
            spec = spec.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), searchRequest.getCostMin()) );
        }
        if (searchRequest.getDurationMax() != null) {
            spec = spec.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.lessThanOrEqualTo(root.get("duration"), searchRequest.getDurationMax()) );
        }
        if (searchRequest.getDurationMin() != null) {
            spec = spec.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), searchRequest.getDurationMin()) );
        }
        if(searchRequest.getDateStartAfter() != null) {
            spec = spec.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("date"), searchRequest.getDateStartAfter()) );
        }
        if (searchRequest.getDateStartBefore() != null) {
            spec = spec.and((root, query, criteriaBuilder)
                    ->criteriaBuilder.lessThanOrEqualTo(root.get("date"), searchRequest.getDateStartBefore()) );
        }

        return eventRepository.findAll(spec)
                .stream()
                .map(eventMapper::toDomain)
                .toList();

    }

    public boolean isLocationHasEvents(Long locationId) {
        return  !eventRepository.findByLocationId(locationId).isEmpty();
    }

    public boolean isLocationHasEventsWithMaxPlacesGreaterThan(Long locationId, Integer capacity) {

        return !eventRepository.findByLocationIdAndMaxPlacesGreaterThan(locationId, capacity).isEmpty();

    }

}
