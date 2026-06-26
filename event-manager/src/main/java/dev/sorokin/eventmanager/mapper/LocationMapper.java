package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.dto.LocationDto;
import dev.sorokin.eventmanager.entity.LocationEntity;
import org.springframework.stereotype.Component;


@Component
public class LocationMapper {


    public LocationDto toDto(Location domain) {

        return  new LocationDto(
                domain.getId(),
                domain.getName(),
                domain.getAddress(),
                domain.getCapacity(),
                domain.getDescription()
        );

    }

    public Location toDomain(LocationDto dto) {

        return  new Location(
                dto.getId(),
                dto.getName(),
                dto.getAddress(),
                dto.getCapacity(),
                dto.getDescription()
        );

    }

    public LocationEntity toEntity(Location domain) {
        return  new LocationEntity(
                domain.getId(),
                domain.getName(),
                domain.getAddress(),
                domain.getCapacity(),
                domain.getDescription()
        );
    }

    public  Location toDomain(LocationEntity entity) {

        return new Location(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCapacity(),
                entity.getDescription()
        );

    }

    public LocationDto toDto(LocationEntity entity) {
        return toDto(toDomain(entity));
    }
}
