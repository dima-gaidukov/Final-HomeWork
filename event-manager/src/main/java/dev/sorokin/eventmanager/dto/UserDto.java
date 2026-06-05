package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.domain.UserRole;

public class UserDto {

    private Long id;

    private String login;

    private Integer age;

    private UserRole role;

    public UserDto(Long id, String login, Integer age, UserRole role) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.role = role;
    }

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
