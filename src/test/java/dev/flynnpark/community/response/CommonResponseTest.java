package dev.flynnpark.community.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonResponseTest {

    @Test
    @DisplayName("성공 응답 - 기본")
    void testSuccess() {
        CommonResponse<Void> response = CommonResponse.success();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getResult()).isNull();
    }

    @Test
    @DisplayName("성공 응답 - 데이터 포함")
    void testSuccessWithData() {
        String data = "testUser";
        CommonResponse<String> response = CommonResponse.success(data);
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getResult()).isEqualTo(data);
    }

    @Test
    @DisplayName("에러 응답 - 코드 및 메시지 포함")
    void testErrorWithCodeAndMessage() {
        int errorCode = 400;
        String errorMessage = "Bad Request";
        CommonResponse<Void> response = CommonResponse.error(errorCode, errorMessage);
        assertThat(response.getCode()).isEqualTo(errorCode);
        assertThat(response.getMessage()).isEqualTo(errorMessage);
        assertThat(response.getResult()).isNull();
    }
}