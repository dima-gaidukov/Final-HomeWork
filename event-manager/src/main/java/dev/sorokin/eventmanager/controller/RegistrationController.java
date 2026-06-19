package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.EventDto;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
@PreAuthorize("hasAnyRole('USER')")
public class RegistrationController {

    private static  final Logger log = LoggerFactory.getLogger(RegistrationController.class);


    private final RegistrationService registrationService;
    private final EventMapper eventMapper;


    public RegistrationController(RegistrationService registrationService, EventMapper eventMapper) {
        this.registrationService = registrationService;
        this.eventMapper = eventMapper;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerForEvent(Authentication auth,@PathVariable Long eventId) {

        log.info("Received request to register for event {}", eventId);

        Long userId = Long.valueOf(auth.getName());

        registrationService.registerForEvent(userId,eventId);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegistration(Authentication auth, @PathVariable Long eventId) {

        log.info("Received request to cancel registration for event {}", eventId);
        Long  userId = Long.valueOf(auth.getName());
        registrationService.cancelRegistration(userId,eventId);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyRegistrations(Authentication auth) {

        log.info("Received request to get registrations for user {}", auth.getName());

        Long userId = Long.valueOf(auth.getName());

        var listRegistr = registrationService.getMyRegistrations(userId);

        var fullList = listRegistr
                .stream()
                .map(eventMapper::toDto)
                .toList();

        return ResponseEntity.ok(fullList);

    }

}
