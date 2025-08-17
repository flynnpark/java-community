package dev.flynnpark.community.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.flynnpark.community.user.dto.UserRegisterRequest;
import dev.flynnpark.community.user.dto.UserResponse;
import dev.flynnpark.community.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;
import dev.flynnpark.community.config.SecurityConfig;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class })
@Import(SecurityConfig.class)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private UserService userService;

        @Test
        @DisplayName("사용자 등록 성공")
        void registerUser_success() throws Exception {
                // Given
                UserRegisterRequest request = new UserRegisterRequest();
                request.setEmail("test@example.com");
                request.setNickname("testuser");
                request.setPassword("password123");

                UserResponse userResponse = UserResponse.builder()
                                .id(1L)
                                .email("test@example.com")
                                .nickname("testuser")
                                .build();

                when(userService.registerUser(any(UserRegisterRequest.class)))
                                .thenReturn(userResponse);

                // When & Then
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print()) // Added for debugging
                                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("사용자 등록 실패 - 유효성 검사 오류 (이메일 형식)")
        void registerUser_validationError_emailFormat() throws Exception {
                // Given
                UserRegisterRequest request = new UserRegisterRequest();
                request.setEmail("invalid-email"); // Invalid email format
                request.setNickname("testuser");
                request.setPassword("password123");

                // When & Then
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print()) // Added for debugging
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("사용자 등록 실패 - 유효성 검사 오류 (필수 필드 누락)")
        void registerUser_validationError_missingField() throws Exception {
                // Given
                UserRegisterRequest request = new UserRegisterRequest();
                request.setEmail("test@example.com");
                // Nickname and password are blank

                // When & Then
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print()) // Added for debugging
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("사용자 등록 실패 - 서비스 로직 오류 (이메일 중복)")
        void registerUser_serviceError_duplicateEmail() throws Exception {
                // Given
                UserRegisterRequest request = new UserRegisterRequest();
                request.setEmail("duplicate@example.com");
                request.setNickname("testuser");
                request.setPassword("password123");

                when(userService.registerUser(any(UserRegisterRequest.class)))
                                .thenThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."));

                // When & Then
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print()) // Added for debugging
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("사용자 등록 실패 - 서비스 로직 오류 (닉네임 중복)")
        void registerUser_serviceError_duplicateNickname() throws Exception {
                // Given
                UserRegisterRequest request = new UserRegisterRequest();
                request.setEmail("test@example.com");
                request.setNickname("duplicateuser");
                request.setPassword("password123");

                when(userService.registerUser(any(UserRegisterRequest.class)))
                                .thenThrow(new IllegalArgumentException("이미 사용 중인 닉네임입니다."));

                // When & Then
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print()) // Added for debugging
                                .andExpect(status().isBadRequest());
        }
}
