package com.example.retailplatform.user.domain.port.in;

import com.example.retailplatform.user.domain.model.User;
import java.util.List;

public interface UserUseCase {

    User createUser(User user);
    User getUserById(String id);
    List<User> getAllUsers();
    User patchUser(String id, User user);   
    User updateUser(String id, User user);
    void softDeleteUser(String id);
}
