package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class JwtResponse {

    private  String jwtToken;

    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public JwtResponse() {
    }


    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
