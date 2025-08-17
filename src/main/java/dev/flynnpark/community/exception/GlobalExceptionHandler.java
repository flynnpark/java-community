package dev.flynnpark.community.exception;

import dev.flynnpark.community.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        CommonResponse<?> response = CommonResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("유효성 검사 실패");

        CommonResponse<?> response = CommonResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        CommonResponse<?> response = CommonResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "인증에 실패했습니다."
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
