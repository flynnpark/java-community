package dev.flynnpark.community.exception;

import dev.flynnpark.community.response.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Objects;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("일반 예외 처리 테스트")
    void handleAllExceptionsTest() {
        Exception ex = new RuntimeException("Test exception");
        ResponseEntity<CommonResponse<?>> responseEntity = globalExceptionHandler.handleAllExceptions(ex);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody()).isNotNull();

        CommonResponse<?> errorResponse = Objects.requireNonNull(responseEntity.getBody());

        assertThat(errorResponse.getCode()).isEqualTo(500);
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred: Test exception");
        assertThat(errorResponse.getResult()).isNull();
    }
}
