package dev.flynnpark.community.exception;

import dev.flynnpark.community.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleAllExceptions(Exception ex) {
        // Log the exception for debugging purposes
        ex.printStackTrace(); // In a real application, use a logger (e.g., SLF4J)

        CommonResponse<?> response = CommonResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // You can add more specific exception handlers here
    // For example, for custom exceptions or common Spring exceptions like MethodArgumentNotValidException
}
