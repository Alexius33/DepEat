package com.alexiusdev.depeat.datamodels;

import java.io.Serializable;
import java.time.LocalDate;

public class User implements Serializable {
    private String email;
    private String id;
    private String name;
    private String surname;
    private LocalDate birthday;
    private Gender gender;

    public User() {
    }

    public User(String email, String id, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public User setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public User setGender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
