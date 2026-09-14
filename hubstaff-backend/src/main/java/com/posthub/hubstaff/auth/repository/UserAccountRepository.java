package com.posthub.hubstaff.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.posthub.hubstaff.auth.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @EntityGraph(attributePaths = {"employee", "roles"})
    Optional<UserAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmployeeId(Long employeeId);

    @Query("select count(u) > 0 from UserAccount u join u.roles r where upper(r.name) = 'ADMIN'")
    boolean existsAdminAccount();
}