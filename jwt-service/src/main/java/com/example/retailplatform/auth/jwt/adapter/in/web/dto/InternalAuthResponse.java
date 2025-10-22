package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternalAuthResponse {
    boolean authenticated;
    InternalJwtUserResponse user;   
}
