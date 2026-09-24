package com.posthub.hubstaff.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "initial_admin_bootstrap")
@Getter
@Setter
public class InitialAdminBootstrapState {

    public static final long INITIAL_ADMIN_ID = 1L;

    @Id
    private Long id;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;
}
