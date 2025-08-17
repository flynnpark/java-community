package dev.flynnpark.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collections;

@RestController
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/healthcheck")
    public ResponseEntity<?> healthcheck() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return ResponseEntity.ok(Collections.singletonMap("status", "UP"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                 .body(Collections.singletonMap("status", "DOWN"));
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                             .body(Collections.singletonMap("status", "DOWN"));
    }
}
