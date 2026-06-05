package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.NotNull;

public class UserCredentials {

    @NotNull
    private  String login;
    @NotNull
    private String password;

    public UserCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserCredentials() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
