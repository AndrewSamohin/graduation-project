package com.example.graduation_project.users;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    @Column(name = "password")
    private String passwordHash;

    private String userRole;

    public UserEntity() {
    }

    public UserEntity(
            Long id, String login, String passwordHash, String userRole) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return userRole;
    }

    public void setRole(String userRole) {
        this.userRole = userRole;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
