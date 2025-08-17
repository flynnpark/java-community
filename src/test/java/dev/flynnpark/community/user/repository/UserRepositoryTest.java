package dev.flynnpark.community.user.repository;

import dev.flynnpark.community.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("이메일로 사용자 찾기 - 성공")
    void findByEmail_success() {
        // Given
        User user = User.builder()
                .email("test@example.com")
                .nickname("testuser")
                .password("encodedPassword")
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 - 실패 (존재하지 않는 이메일)")
    void findByEmail_notFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재함")
    void existsByEmail_true() {
        // Given
        User user = User.builder()
                .email("exists@example.com")
                .nickname("existsuser")
                .password("encodedPassword")
                .build();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail("exists@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않음")
    void existsByEmail_false() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재함")
    void existsByNickname_true() {
        // Given
        User user = User.builder()
                .email("nick@example.com")
                .nickname("existsnick")
                .password("encodedPassword")
                .build();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByNickname("existsnick");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하지 않음")
    void existsByNickname_false() {
        // When
        boolean exists = userRepository.existsByNickname("nonexistentnick");

        // Then
        assertThat(exists).isFalse();
    }
}
