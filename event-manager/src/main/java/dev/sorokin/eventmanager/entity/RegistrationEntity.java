package dev.sorokin.eventmanager.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "registrations", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id","user_id"}))
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Long userId;

    public RegistrationEntity() {
    }

    public RegistrationEntity(Long id, Long eventId, Long userId) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
