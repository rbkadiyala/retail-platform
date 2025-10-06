package com.example.retailplatform.user.domain.port.out;

import com.example.retailplatform.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    List<User> findAllActive();  
    Optional<User> findActiveById(String id);
    Optional<User> findActiveByUsername(String username);
    Optional<User> findActiveByEmail(String email);
    Optional<User> findActiveByPhoneNumber(String phoneNumber);  

    boolean existsByUsername(String username);
    boolean existsByActiveEmail(String email);
    boolean existsByActivePhoneNumber(String phoneNumber);

    User save(User user);
    User patch(User user);
    void softDelete(String id);
}
