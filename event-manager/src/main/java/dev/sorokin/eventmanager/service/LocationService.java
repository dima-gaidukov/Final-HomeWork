package dev.sorokin.eventmanager.service;
import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.exception.ResourceNotFoundException;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.repository.LocationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    private final EventService eventService;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper, EventService eventService) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.eventService = eventService;
    }

    @CacheEvict(value = "locations",allEntries = true)
    public Location createLocation(Location createdLocation) {

        var locationEntity = locationMapper.toEntity(createdLocation);

        var saveLocationResult = locationRepository.save(locationEntity);

        return locationMapper.toDomain(saveLocationResult);

    }

    @Cacheable("locations")
    public Location getLocationById(Long id) {

        Optional<LocationEntity> foundEntity = locationRepository.findById(id);

        if(foundEntity.isPresent()) {
            LocationEntity entity = foundEntity.get();
            return locationMapper.toDomain(entity);
        }else  {
            throw new ResourceNotFoundException("Location with id " + id + " not found");
        }

    }

    @Cacheable("locations")
    public List<Location> getAllLocations() {

        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toDomain)
                .toList();

    }

    @CacheEvict(value = "locations",allEntries = true)
    public Location updateLocation(Long id, Location location) {



        LocationEntity entity = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location with id %d not found".formatted(id)));

        if(location.getCapacity() < entity.getCapacity()){
            if(eventService.isLocationHasEventsWithMaxPlacesGreaterThan(id, location.getCapacity() )){
                throw new IllegalArgumentException("Cannot reduce capacity, events require more places");
            }
        }

        entity.setName(location.getName());
        entity.setAddress(location.getAddress());
        entity.setCapacity(location.getCapacity());
        entity.setDescription(location.getDescription());

        return  locationMapper.toDomain(locationRepository.save(entity));
    }

    @CacheEvict(value = "locations",allEntries = true)
    public void deleteLocation(Long id) {

        if(eventService.isLocationHasEvents(id)){
            throw  new IllegalArgumentException("Cannot delete location with existing events");
        }

        LocationEntity entity = locationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Location with id %d not found".formatted(id)));
        locationRepository.delete(entity);
    }



}
