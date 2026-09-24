package com.posthub.hubstaff.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.posthub.hubstaff.auth.entity.InitialAdminBootstrapState;

public interface InitialAdminBootstrapRepository extends JpaRepository<InitialAdminBootstrapState, Long> {
}
