package dev.sorokin.eventmanager.mapper;
import dev.sorokin.eventmanager.domain.Registration;
import dev.sorokin.eventmanager.entity.RegistrationEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public RegistrationEntity toEntity(Registration domain){

        return new RegistrationEntity(
                domain.getId(),
                domain.getEventId(),
                domain.getUserId()
        );
    }

    public Registration toDomain(RegistrationEntity toEntity){

        return new Registration(
                toEntity.getId(),
                toEntity.getEventId(),
                toEntity.getUserId()
        );
    }

}
