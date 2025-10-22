package com.example.retailplatform.user.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    // For service lookups → active users only
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.active = true")
    Optional<UserEntity> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.active = true")
    Optional<UserEntity> findActiveByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u WHERE u.phoneNumber = :phoneNumber AND u.active = true")
    Optional<UserEntity> findActiveByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT u FROM UserEntity u WHERE u.active = true")
    List<UserEntity> findAllActive();

    @Query("SELECT u FROM UserEntity u WHERE u.id = :id AND u.active = true")
    Optional<UserEntity> findActiveById(@Param("id") Long id);

    // For uniqueness check (global username, including inactive users)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.email = :email AND u.active = true")
    boolean existsByActiveEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.phoneNumber = :phoneNumber AND u.active = true")
    boolean existsByActivePhoneNumber(@Param("phoneNumber") String phoneNumber);

    // -------------------- ✨ Flexible Search (Dynamic) --------------------
    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.active = true
          AND (:username IS NULL OR u.username = :username)
          AND (:email IS NULL OR u.email = :email)
          AND (:phoneNumber IS NULL OR u.phoneNumber = :phoneNumber)
    """)
    List<UserEntity> searchActiveUsers(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber
    );
}
