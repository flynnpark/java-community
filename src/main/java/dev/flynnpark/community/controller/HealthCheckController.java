package dev.flynnpark.community.controller;

import dev.flynnpark.community.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/healthcheck")
    public CommonResponse<String> healthcheck() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return CommonResponse.success("UP");
            }
        }
        // If connection is not valid or an exception occurs, let it propagate to
        // GlobalExceptionHandler
        throw new Exception("Database connection is not valid or unavailable.");
    }
}
