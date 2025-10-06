package com.example.retailplatform.user.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message; 
    private String key;     
    private String field;
    private String path;

    @JsonProperty("errors") // Serialize validationErrors as "errors" in JSON
    private List<ValidationError> validationErrors;

    public List<ValidationError> getValidationErrors() {
        return validationErrors == null ? null : Collections.unmodifiableList(validationErrors);
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors == null ? null : new ArrayList<>(validationErrors);
    }

    public static class ErrorResponseBuilder {
        public ErrorResponseBuilder validationErrors(List<ValidationError> validationErrors) {
            this.validationErrors = validationErrors == null ? null : new ArrayList<>(validationErrors);
            return this;
        }
    }
}
