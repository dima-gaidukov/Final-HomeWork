package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<Location> getAllById(Long id);
}
