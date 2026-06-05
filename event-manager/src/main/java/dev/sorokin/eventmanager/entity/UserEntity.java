package dev.sorokin.eventmanager.entity;


import dev.sorokin.eventmanager.domain.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String login;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private  String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserEntity(Long id, String login, Integer age, String passwordHash, UserRole role) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UserEntity() {
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
