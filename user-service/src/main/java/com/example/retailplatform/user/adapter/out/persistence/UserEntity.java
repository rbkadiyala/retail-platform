package com.example.retailplatform.user.adapter.out.persistence;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Default
    private boolean active = true; // default active for new users

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore
    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    @Default
    private boolean passwordChangeRequired = true; // always true by default

    @Column(nullable = false)
    @Default
    private String createdBy = UserConstants.SYSTEM;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Default
    private String updatedBy = UserConstants.SYSTEM;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.active = false; // set inactive on deletion
        this.status = Status.DELETED;
        onUpdate();
    }

    public void markPasswordChangeRequired() {
        this.passwordChangeRequired = true;
        onUpdate();
    }

    public void clearPasswordChangeRequired() {
        this.passwordChangeRequired = false;
        onUpdate();
    }
}
