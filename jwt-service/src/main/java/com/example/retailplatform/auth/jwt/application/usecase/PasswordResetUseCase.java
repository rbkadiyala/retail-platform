package com.example.retailplatform.auth.jwt.application.usecase;

import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalPasswordResetRequest;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalUpdatePasswordRequest;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.PasswordResetTokenResponse;

public interface PasswordResetUseCase {
    PasswordResetTokenResponse requestPasswordReset(InternalPasswordResetRequest request);
    void resetPassword(InternalUpdatePasswordRequest request);
}
