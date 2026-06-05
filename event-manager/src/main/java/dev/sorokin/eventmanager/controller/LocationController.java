package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.dto.LocationDto;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private static  final Logger log = LoggerFactory.getLogger(LocationController.class);
    private final LocationService locationService;
    private final LocationMapper locationMapper;

    public LocationController(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@RequestBody @Valid LocationDto locationDto) {

        log.info("Input locationDto: {}", locationDto);

        Location createdLocation = locationService.createLocation(locationMapper.toDomain(locationDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(locationMapper.toDto(createdLocation));

    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {

        log.info("Getting all locations");

        List<LocationDto> locations = locationService.getAllLocations()
                .stream()
                .map(locationMapper::toDto)
                .toList();
        return ResponseEntity.ok(locations);


    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        log.info("Getting location by id: {}", id);
        Location location = locationService.getLocationById(id);
        LocationDto locationDto = locationMapper.toDto(location);
        return ResponseEntity.ok(locationDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocationById(@PathVariable Long id,
                                                          @RequestBody @Valid LocationDto locationDto) {

        log.info("Input locationDto: {}", locationDto);

        Location location = locationMapper.toDomain(locationDto);
        Location updatedLocation = locationService.updateLocation(id, location);
        LocationDto result = locationMapper.toDto(updatedLocation);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationById(@PathVariable Long id) {
        log.info("Deleting location by id: {}", id);
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

}
