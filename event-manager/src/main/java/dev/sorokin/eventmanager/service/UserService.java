package dev.sorokin.eventmanager.service;



import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.domain.UserRole;
import dev.sorokin.eventmanager.dto.JwtResponse;
import dev.sorokin.eventmanager.dto.UserCredentials;
import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.dto.UserRegistration;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.security.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserDto userRegister (@Valid UserRegistration userReg){

       if( userRepository.existsByLogin(userReg.getLogin())){

           throw new IllegalArgumentException("User already exists");
        }

       var passEncod = passwordEncoder.encode(userReg.getPassword());

       var userToSave = new User(
               null,
               userReg.getLogin(),
               userReg.getAge(),
               passEncod,
               UserRole.USER
       );

       var userToEntity = userMapper.toEntity(userToSave);

       var save = userRepository.save(userToEntity);

       return userMapper.toDto(userMapper.toDomain(save));

    }

    public JwtResponse authenticate(UserCredentials userAuth){

        var userBySearch = userRepository.findByLogin(userAuth.getLogin());

        if(userBySearch.isEmpty()){
            throw  new IllegalArgumentException("User not found");
        }

        var userPassword = userBySearch.get().getPasswordHash();
        if(!passwordEncoder.matches(userAuth.getPassword(),userPassword)){
            throw new IllegalArgumentException("Wrong password");
        }

        var userToken = userBySearch.get();
        var token = jwtTokenProvider.generateToken(userToken.getId(),userToken.getLogin(),userToken.getRole());

        return new JwtResponse(token);

    }

    public UserDto getUserById(Long id){
        var user =  userRepository.findById(id);
        if(user.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        return userMapper.toDto(userMapper.toDomain(user.get()));
    }

    @PostConstruct
    public void seedAdmin(){

        if(userRepository.findByLogin("admin").isPresent()){
            return;
        }else {
            var admin = new UserEntity(null,"admin",18,passwordEncoder.encode("admin"),UserRole.ADMIN);
            userRepository.save(admin);
        }

    }



}
