package com.example.retailplatform.user.adapter.out.persistence;

import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityMapperTest {

    private UserEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserEntityMapper();
    }

    static Stream<FieldUpdateTestData> provideFieldUpdateTestData() {
        return Stream.of(
                new FieldUpdateTestData("firstName", "Alice"),
                new FieldUpdateTestData("lastName", "Smith"),
                new FieldUpdateTestData("username", "alice123"),
                new FieldUpdateTestData("email", "alice@example.com"),
                new FieldUpdateTestData("phoneNumber", "1234567890"),
                new FieldUpdateTestData("status", Status.ACTIVE),
                new FieldUpdateTestData("role", Role.ADMIN),
                new FieldUpdateTestData("active", true),
                new FieldUpdateTestData("active", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideFieldUpdateTestData")
    void updateEntityFromModel_individualFields(FieldUpdateTestData testData) {
        UserEntity entity = UserEntity.builder()
                .firstName("Old")
                .lastName("Old")
                .username("oldUser")
                .email("old@example.com")
                .phoneNumber("000")
                .status(Status.INACTIVE)
                .role(Role.USER)
                .deleted(false)
                .build();

        User user = User.builder()
                .firstName("firstName".equals(testData.field) ? (String) testData.value : null)
                .lastName("lastName".equals(testData.field) ? (String) testData.value : null)
                .username("username".equals(testData.field) ? (String) testData.value : null)
                .email("email".equals(testData.field) ? (String) testData.value : null)
                .phoneNumber("phoneNumber".equals(testData.field) ? (String) testData.value : null)
                .status("status".equals(testData.field) ? (Status) testData.value : null)
                .role("role".equals(testData.field) ? (Role) testData.value : null)
                .active("active".equals(testData.field) ? (Boolean) testData.value : null)
                .build();

        mapper.updateEntityFromModel(user, entity);

        switch (testData.field) {
            case "firstName" -> assertEquals(testData.value, entity.getFirstName());
            case "lastName" -> assertEquals(testData.value, entity.getLastName());
            case "username" -> assertEquals(testData.value, entity.getUsername());
            case "email" -> assertEquals(testData.value, entity.getEmail());
            case "phoneNumber" -> assertEquals(testData.value, entity.getPhoneNumber());
            case "status" -> assertEquals(testData.value, entity.getStatus());
            case "role" -> assertEquals(testData.value, entity.getRole());
            case "active" -> {
                Boolean active = (Boolean) testData.value;
                assertEquals(!active, entity.isDeleted(), "Active field should map to deleted correctly");
            }
        }
    }

    // Helper class to hold field name and value
    private static class FieldUpdateTestData {
        final String field;
        final Object value;

        FieldUpdateTestData(String field, Object value) {
            this.field = field;
            this.value = value;
        }
    }
}
