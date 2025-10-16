package com.example.retailplatform.user.common;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void testOfMethodCreatesErrorResponse() {
        ErrorResponse.Error error = ErrorResponse.Error.builder()
                .fieldName("email")
                .fieldValue("invalid-email")
                .message("Email must be valid")
                .build();

        ErrorResponse response = ErrorResponse.of(
                400,
                "VALIDATION_FAILED",
                "validation.failed",
                "/api/users",
                "Validation failed",
                "User",
                List.of(error)
        );

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("VALIDATION_FAILED");
        assertThat(response.getErrors()).hasSize(1)
                .first()
                .extracting(ErrorResponse.Error::getFieldName)
                .isEqualTo("email");
    }
}
