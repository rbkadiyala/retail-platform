package com.example.retailplatform.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.retailplatform.user.config.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
        // Test just ensures the Spring context loads without errors 
    }
}
