package com.example.retailplatform.auth.jwt.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserListResponse {
    private Embedded _embedded;

    @Data
    public static class Embedded {
        private List<UserDto> userList;
    }
}