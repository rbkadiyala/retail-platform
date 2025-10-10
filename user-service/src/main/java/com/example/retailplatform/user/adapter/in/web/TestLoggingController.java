package com.example.retailplatform.user.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestLoggingController {

    @GetMapping("/api/test-logging")
    public String testLogging() {
        return "Logging filter is working!";
    }
}