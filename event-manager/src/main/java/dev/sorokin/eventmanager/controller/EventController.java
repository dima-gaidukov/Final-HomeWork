package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.dto.EventDto;
import dev.sorokin.eventmanager.dto.EventSearchRequestDto;
import dev.sorokin.eventmanager.dto.EventUpdateRequestDto;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private static  final Logger log = LoggerFactory.getLogger(EventController.class);

    private  final EventService eventService;

    private final EventMapper eventMapper;
    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventCreateRequestDto dto, Authentication auth) {

        log.info("Received request to create event");

        Long userId = Long.valueOf(auth.getName());

        Event eventDomain = eventMapper.toDomain(userId,dto);

        Event result = eventService.createEvent(userId,eventDomain);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toDto(result));

    }


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {

        log.info("Received request to get event by id");

        Event event = eventService.getEventById(id);

        EventDto eventDto = eventMapper.toDto(event);

        return ResponseEntity.ok(eventDto);

    }


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id,
                                                @Valid @RequestBody EventUpdateRequestDto dto,
                                                Authentication auth) {

        log.info("Received request to update event by id");

        Long userId = Long.valueOf(auth.getName());

        Event current = eventService.getEventById(id);

        Event merge = eventMapper.merge(current, dto);

        Event update = eventService.updateEvent(id,userId,merge);

        return ResponseEntity.ok(eventMapper.toDto(update));


    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long id,Authentication auth) {

        log.info("Received request to delete event by id");

        Long userId = Long.valueOf(auth.getName());

        eventService.deleteEvent(id,userId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyEvents(Authentication auth) {

        log.info("Received request to get events by user");

        Long userId = Long.valueOf(auth.getName());

        List<Event> events = eventService.getMyEvents(userId);

        List<EventDto> eventMyDto = events.stream().map(eventMapper::toDto).toList();

        return ResponseEntity.ok(eventMyDto);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(@RequestBody EventSearchRequestDto request) {

        log.info("Received request to search events");

        List<Event> eventsSearch = eventService.searchEvents(request);

        List<EventDto> eventSearchDto = eventsSearch.stream().map(eventMapper::toDto).toList();

        return ResponseEntity.ok(eventSearchDto);
    }


}
