package com.example.retailplatform.user.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String messageKey;
    private String message;
    private String path;
    private String resource;

    @Builder.Default
    private List<Error> errors = Collections.emptyList();

    // ------------------ Nested Error ------------------
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        private String fieldName;
        private Object fieldValue;
        private String message;
    }

    // ------------------ Static helpers ------------------

    public static ErrorResponse of(int status, String error, String messageKey, String path, String message, String resource, List<Error> errors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .messageKey(messageKey)
                .message(message)
                .path(path)
                .resource(resource)
                .errors(errors != null ? errors : Collections.emptyList())
                .build();
    }
}
