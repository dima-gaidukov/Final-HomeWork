package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.entity.UserEntity;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    public UserEntity toEntity(User user){

        return new  UserEntity(
                user.getId(),
                user.getLogin(),
                user.getAge(),
                user.getPasswordHash(),
                user.getRole()
        );

    }

    public User toDomain (UserEntity userEntity){

        return new User (
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getAge(),
                userEntity.getPasswordHash(),
                userEntity.getRole()
        );

    }

    public UserDto  toDto (User user){
        return new UserDto (
                user.getId(),
                user.getLogin(),
                user.getAge(),
                user.getRole()
        );
    }

}
