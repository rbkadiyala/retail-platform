package com.example.retailplatform.user.domain.port.in;

import com.example.retailplatform.user.adapter.in.web.dto.AuthRequest;
import com.example.retailplatform.user.adapter.in.web.dto.AuthResponse;
import com.example.retailplatform.user.domain.model.User;
import java.util.List;

public interface UserUseCase {

    User createUser(User user);
    User getUserById(String id);
    List<User> getAllUsers();
    User patchUser(String id, User user);   
    User updateUser(String id, User user);
    void softDeleteUser(String id);
    List<User> searchUsers(String username, String email, String phoneNumber);

    AuthResponse authenticate(AuthRequest authRequest);

}
