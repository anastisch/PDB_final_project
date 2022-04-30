package com.pdb.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "username")
    private String username;
    @Column(name = "first_name")
    private String firstName;
    private String surname;
    private String patronymic;
    @Column(name = "birth_day")
    private Date birthDay;
    private String email;
    private String phone;
    @JsonIgnore
    private String password;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
        username = firstName = surname = patronymic = email = phone = password = "";
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String firstName, String surname, String patronymic, Date birthDay, String email, String phone, String password) {
        this.username = username;
        this.firstName = firstName;
        this.surname = surname;
        this.patronymic = patronymic;
        this.birthDay = birthDay;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public boolean isAdmin() {
        boolean isAdmin = false;
        for (Role role: roles) {
            if (role.getName().equals(ERole.ROLE_ADMIN)) {
                isAdmin = true;
                break;
            }
        }
        return isAdmin;
    }
    public boolean isModerator() {
        boolean isModerator = false;
        for (Role role: roles) {
            if (role.getName().equals(ERole.ROLE_MODERATOR)) {
                isModerator = true;
                break;
            }
        }
        return isModerator;
    }
    public boolean isUser() {
        boolean isUser = false;
        for (Role role: roles) {
            if (role.getName().equals(ERole.ROLE_USER)) {
                isUser = true;
                break;
            }
        }
        return isUser;
    }
}