package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

public class EventUpdateRequestDto {

    private String name;

    @Min(1)
    private Integer maxPlaces;

    @Future
    private LocalDateTime date;

    @Min(1)
    private Integer cost;

    @Min(30)
    private Integer duration;

    private Long locationId;

    public EventUpdateRequestDto() {
    }

    public EventUpdateRequestDto( String name, Integer maxPlaces, LocalDateTime date,
                                  Integer cost, Integer duration, Long locationId) {
        this.name = name;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
