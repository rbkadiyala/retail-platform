package com.example.retailplatform.user.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationError {
    private String field;   // field name
    private Object value;   // rejected value
    private String message; // validation message
}
