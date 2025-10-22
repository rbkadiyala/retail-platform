package com.example.retailplatform.user.domain.port.out;

import com.example.retailplatform.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    // -------------------- Create / Existence Checks --------------------
    boolean existsByUsername(String username);
    boolean existsByActiveEmail(String email);
    boolean existsByActivePhoneNumber(String phoneNumber);

    // -------------------- Read --------------------
    Optional<User> findActiveById(String id);
    List<User> findAllActive();

    // -------------------- Update / Patch --------------------
    Optional<User> findActiveByUsername(String username);
    Optional<User> findActiveByEmail(String email);
    Optional<User> findActiveByPhoneNumber(String phoneNumber);

    User save(User user);      // For create
    User patch(User user);     // For update / patch

    // -------------------- Delete --------------------
    void softDelete(String id);

    // -------------------- Search --------------------
    List<User> searchActiveUsers(String username, String email, String phoneNumber);
}
