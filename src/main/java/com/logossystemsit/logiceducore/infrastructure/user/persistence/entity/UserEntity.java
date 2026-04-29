package com.logossystemsit.logiceducore.infrastructure.user.persistence.entity;

import com.logossystemsit.logiceducore.domain.user.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private String id;
    private String username;
    private String email;
    private String passwordHash;

    private String firstGivenName;
    private String secondGivenName;
    private String firstFamilyName;
    private String secondFamilyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private User.Sex sex;

    private LocalDate birthDate;

    private String documentType;
    private String documentValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private User.Status status;

    private Instant createdAt;
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstGivenName() {
        return firstGivenName;
    }

    public String getSecondGivenName() {
        return secondGivenName;
    }

    public String getFirstFamilyName() {
        return firstFamilyName;
    }

    public String getSecondFamilyName() {
        return secondFamilyName;
    }

    public User.Sex getSex() {
        return sex;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentValue() {
        return documentValue;
    }

    public User.Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /* ------------------ SETTERS ------------------ */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(User.Status status) { this.status = status; }

    public void setDocumentValue(String documentValue) {
        this.documentValue = documentValue;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setSex(User.Sex sex) { this.sex = sex; }

    public void setSecondFamilyName(String secondFamilyName) {
        this.secondFamilyName = secondFamilyName;
    }

    public void setFirstFamilyName(String firstFamilyName) {
        this.firstFamilyName = firstFamilyName;
    }

    public void setSecondGivenName(String secondGivenName) {
        this.secondGivenName = secondGivenName;
    }

    public void setFirstGivenName(String firstGivenName) {
        this.firstGivenName = firstGivenName;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }
}
