package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

@Component
public class UserRegistration {

    @NotNull
    @Size(min = 3, max = 50)
    private  String login;

    @NotNull
    @Size(min = 6, max = 100)
    private  String password;

    @NotNull
    @Min(18)
    private Integer age;

    public UserRegistration(String login, String password, Integer age) {
        this.login = login;
        this.password = password;
        this.age = age;
    }

    public UserRegistration() {
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
