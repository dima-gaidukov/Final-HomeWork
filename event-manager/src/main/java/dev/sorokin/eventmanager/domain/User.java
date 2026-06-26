package dev.sorokin.eventmanager.domain;

public class User {

    private Long id;

    private String login;

    private Integer age;

    private String passwordHash;

    private UserRole role;


    public User(Long id, String login, Integer age, String passwordHash, UserRole role) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User() {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
