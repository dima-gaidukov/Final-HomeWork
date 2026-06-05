package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.JwtResponse;
import dev.sorokin.eventmanager.dto.UserCredentials;
import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.dto.UserRegistration;
import dev.sorokin.eventmanager.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {

    private static  final Logger log = LoggerFactory.getLogger(AuthController.class);

    private  final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping
    public ResponseEntity<UserDto> userRegister (@RequestBody @Valid UserRegistration userReg){

        log.info("Registering user {}",userReg.getLogin());
        var result = userService.userRegister(userReg);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }

    @PostMapping("/auth")
    public ResponseEntity<JwtResponse> authenticate (@RequestBody @Valid UserCredentials userAuth){

        log.info("Authenticating user {}",userAuth.getLogin());
        var token = userService.authenticate(userAuth);
        return ResponseEntity.ok(token);

    }

}
