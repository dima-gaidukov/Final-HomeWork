package dev.sorokin.eventmanager.mapper;


import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.domain.EventStatus;
import dev.sorokin.eventmanager.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.dto.EventDto;
import dev.sorokin.eventmanager.dto.EventUpdateRequestDto;
import dev.sorokin.eventmanager.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventEntity toEntity(Event domain){

        return new EventEntity(
                domain.getId(),
                domain.getName(),
                domain.getOwnerId(),
                domain.getMaxPlaces(),
                domain.getOccupiedPlaces(),
                domain.getDate(),
                domain.getCost(),
                domain.getDuration(),
                domain.getLocationId(),
                domain.getStatus()
        );
    }

    public Event toDomain (EventEntity toEntity){

        return new Event(
                toEntity.getId(),
                toEntity.getName(),
                toEntity.getOwnerId(),
                toEntity.getMaxPlaces(),
                toEntity.getOccupiedPlaces(),
                toEntity.getDate(),
                toEntity.getCost(),
                toEntity.getDuration(),
                toEntity.getLocationId(),
                toEntity.getStatus()
        );
    }

    public EventDto toDto (Event domain){

        return new EventDto(
                domain.getId(),
                domain.getName(),
                domain.getOwnerId(),
                domain.getMaxPlaces(),
                domain.getOccupiedPlaces(),
                domain.getDate(),
                domain.getCost(),
                domain.getDuration(),
                domain.getLocationId(),
                domain.getStatus()
        );
    }

    public Event toDomain(Long userId, EventCreateRequestDto dto) {
        return new Event(null,
                dto.getName(),
                userId,
                dto.getMaxPlaces(), 0,
                dto.getDate(),
                dto.getCost(),
                dto.getDuration(),
                dto.getLocationId(),
                EventStatus.WAIT_START);
    }

    public Event merge(Event current, EventUpdateRequestDto dto){

        if(dto.getName() != null) current.setName(dto.getName());
        if(dto.getMaxPlaces() != null) current.setMaxPlaces(dto.getMaxPlaces());
        if(dto.getCost() != null) current.setCost(dto.getCost());
        if(dto.getDuration() != null) current.setDuration(dto.getDuration());
        if(dto.getDate() != null) current.setDate(dto.getDate());
        if(dto.getLocationId() != null) current.setLocationId(dto.getLocationId());
        return  current;

    }

}
