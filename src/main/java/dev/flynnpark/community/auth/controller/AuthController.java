package dev.flynnpark.community.auth.controller;

import dev.flynnpark.community.auth.dto.LoginRequest;
import dev.flynnpark.community.auth.dto.LoginResponse;
import dev.flynnpark.community.auth.service.AuthService;
import dev.flynnpark.community.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(CommonResponse.success(HttpStatus.OK.value(), "Login successful", loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<?>> logout() {
        // JWT 기반 인증에서는 클라이언트 측에서 토큰을 삭제하는 것으로 로그아웃 처리
        // 서버 측에서 추가적인 토큰 무효화 로직이 필요하다면 여기에 구현
        return ResponseEntity.ok(CommonResponse.success(HttpStatus.OK.value(), "Logout successful", null));
    }
}
